-- ============================================================
-- V2__create_assets.sql
-- Requirements: FA-01, FA-02, FA-03, FA-04
-- ============================================================

-- Asset categories (e.g. Machinery, Vehicle, Equipment, Building)
-- Aligned with Thong tu 45/2013/TT-BTC asset classifications
CREATE TABLE asset_categories (
    id                  SERIAL PRIMARY KEY,
    code                VARCHAR(20)  NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    useful_life_min     INTEGER,     -- minimum useful life in years (per TT45)
    useful_life_max     INTEGER,     -- maximum useful life in years (per TT45)
    depreciation_method VARCHAR(20)  NOT NULL DEFAULT 'STRAIGHT_LINE', -- STRAIGHT_LINE | DECLINING_BALANCE
    description         TEXT
);

-- Seed standard asset categories per TT45/2013
INSERT INTO asset_categories (code, name, useful_life_min, useful_life_max, depreciation_method) VALUES
    ('MACHINE',   'Máy móc, thiết bị',              5,  15, 'STRAIGHT_LINE'),
    ('VEHICLE',   'Phương tiện vận tải',            6,  10, 'STRAIGHT_LINE'),
    ('BUILDING',  'Nhà cửa, vật kiến trúc',        25,  50, 'STRAIGHT_LINE'),
    ('EQUIPMENT', 'Thiết bị văn phòng',             3,   5, 'STRAIGHT_LINE'),
    ('IT',        'Thiết bị công nghệ thông tin',   3,   5, 'STRAIGHT_LINE'),
    ('OTHER',     'Tài sản khác',                   5,  10, 'STRAIGHT_LINE');

-- Fixed assets — FA-01 (digital profile)
CREATE TABLE assets (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Identity
    asset_code              VARCHAR(50)     NOT NULL UNIQUE,  -- e.g. TS-2024-001
    name                    VARCHAR(255)    NOT NULL,
    category_id             INTEGER         NOT NULL REFERENCES asset_categories(id),
    managing_unit_id        UUID            NOT NULL REFERENCES managing_units(id),

    -- Technical parameters (FA-01)
    serial_number           VARCHAR(100),
    manufacturer            VARCHAR(255),
    model                   VARCHAR(255),
    country_of_origin       VARCHAR(100),
    technical_specs         TEXT,           -- free-text for detailed technical parameters
    location                VARCHAR(255),   -- physical location within the unit

    -- Financial (FA-01, FA-02)
    original_cost           NUMERIC(18, 2)  NOT NULL,         -- nguyen gia
    acquisition_date        DATE            NOT NULL,
    funding_source          VARCHAR(100),   -- e.g. 'Ngan sach nha nuoc', 'Von tu co'
    useful_life_years       INTEGER         NOT NULL,         -- as agreed at time of registration
    salvage_value           NUMERIC(18, 2)  NOT NULL DEFAULT 0,
    depreciation_method     VARCHAR(20)     NOT NULL DEFAULT 'STRAIGHT_LINE',

    -- Depreciation computed fields (FA-02) — updated by DepreciationService
    accumulated_depreciation NUMERIC(18, 2) NOT NULL DEFAULT 0,
    net_book_value           NUMERIC(18, 2) NOT NULL DEFAULT 0,
    -- net_book_value = original_cost - accumulated_depreciation
    depreciation_start_date  DATE,
    depreciation_end_date    DATE,
    annual_depreciation_rate NUMERIC(8, 4), -- percentage e.g. 20.0000 for 20%

    -- Operational status (FA-03)
    status                  VARCHAR(30)     NOT NULL DEFAULT 'IN_USE',
    -- IN_USE | MAINTENANCE | IDLE | TRANSFERRED | LIQUIDATED
    status_reason           TEXT,
    status_changed_at       TIMESTAMP,
    status_changed_by       VARCHAR(100),

    -- Supporting documents
    purchase_document_ref   VARCHAR(255),   -- invoice / procurement order number
    notes                   TEXT,

    -- Audit fields
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100)    NOT NULL
);

-- Asset lifecycle history FA-04 (immutable, append-only)
-- Every change to any asset field writes a new row here
CREATE TABLE asset_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asset_id        UUID            NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    event_type      VARCHAR(50)     NOT NULL,
    -- CREATED | STATUS_CHANGED | COST_UPDATED | REVALUED |
    -- TRANSFERRED | DEPRECIATION_POSTED | LIQUIDATED
    description     TEXT            NOT NULL,
    old_value       TEXT,           -- JSON string of changed fields before
    new_value       TEXT,           -- JSON string of changed fields after
    performed_by    VARCHAR(100)    NOT NULL,
    performed_at    TIMESTAMP       NOT NULL DEFAULT NOW()
    -- NO updated_at — this table is append-only by design
);

-- Indexes
CREATE INDEX idx_assets_code            ON assets(asset_code);
CREATE INDEX idx_assets_unit            ON assets(managing_unit_id);
CREATE INDEX idx_assets_category        ON assets(category_id);
CREATE INDEX idx_assets_status          ON assets(status);
CREATE INDEX idx_assets_acquisition     ON assets(acquisition_date);
CREATE INDEX idx_asset_history_asset    ON asset_history(asset_id);
CREATE INDEX idx_asset_history_event    ON asset_history(event_type);
CREATE INDEX idx_asset_history_date     ON asset_history(performed_at);