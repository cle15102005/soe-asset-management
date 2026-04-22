-- ============================================================
-- V4__create_handover_liquidation.sql
-- Author: Le Viet Cuong (M1)
-- Module owner: Linh (M4)
-- Requirements: HL-01, HL-02, HL-03
-- ============================================================

-- ============================================================
-- HANDOVER — HL-01
-- Workflow: DRAFT → PENDING_APPROVAL → APPROVED → CONFIRMED → COMPLETED
--           or any step → REJECTED
-- ============================================================
CREATE TABLE handover_requests (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    request_code            VARCHAR(50)     NOT NULL UNIQUE, -- e.g. BG-2024-001
    asset_id                UUID            NOT NULL REFERENCES assets(id),

    -- Parties involved
    from_unit_id            UUID            NOT NULL REFERENCES managing_units(id),
    to_unit_id              UUID            NOT NULL REFERENCES managing_units(id),
    initiated_by            VARCHAR(100)    NOT NULL,   -- username of initiator

    -- Workflow status — HL-01
    status                  VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    -- DRAFT | PENDING_APPROVAL | APPROVED | CONFIRMED | COMPLETED | REJECTED

    -- Justification and context
    reason                  TEXT            NOT NULL,
    handover_date           DATE,           -- planned or actual handover date
    asset_condition         VARCHAR(50),    -- GOOD | FAIR | POOR
    notes                   TEXT,

    -- Step 1: Department head approval
    dept_approved_by        VARCHAR(100),
    dept_approved_at        TIMESTAMP,
    dept_approval_notes     TEXT,

    -- Step 2: Receiving unit confirmation — HL-01
    confirmed_by            VARCHAR(100),
    confirmed_at            TIMESTAMP,
    confirmation_notes      TEXT,

    -- Step 3: Final record update completion
    completed_by            VARCHAR(100),
    completed_at            TIMESTAMP,

    -- Rejection (any step)
    rejected_by             VARCHAR(100),
    rejected_at             TIMESTAMP,
    rejection_reason        TEXT,

    -- HL-03: Generated handover document reference
    document_ref            VARCHAR(255),   -- Bien ban ban giao document number
    document_generated_at   TIMESTAMP,
    document_signed         BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Audit fields
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100)    NOT NULL
);

-- ============================================================
-- LIQUIDATION — HL-02, HL-03
-- Workflow: DRAFT → PENDING_MANAGER → PENDING_DIRECTOR → APPROVED → COMPLETED
--           or any step → REJECTED
-- ============================================================
CREATE TABLE liquidation_requests (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    request_code            VARCHAR(50)     NOT NULL UNIQUE, -- e.g. TL-2024-001
    asset_id                UUID            NOT NULL REFERENCES assets(id),
    requesting_unit_id      UUID            NOT NULL REFERENCES managing_units(id),
    initiated_by            VARCHAR(100)    NOT NULL,

    -- Workflow status — HL-02
    status                  VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    -- DRAFT | PENDING_MANAGER | PENDING_DIRECTOR | APPROVED | COMPLETED | REJECTED

    -- HL-02: Justification details
    justification           TEXT            NOT NULL,   -- reason for liquidation
    asset_condition         VARCHAR(50)     NOT NULL,   -- GOOD | FAIR | POOR | DAMAGED
    current_market_value    NUMERIC(18, 2),             -- estimated current value
    disposal_method         VARCHAR(30)     NOT NULL,   -- AUCTION | SCRAP | DONATION
    disposal_notes          TEXT,

    -- Step 1: Asset manager approval — HL-02
    manager_approved_by     VARCHAR(100),
    manager_approved_at     TIMESTAMP,
    manager_notes           TEXT,

    -- Step 2: Director/board approval — HL-02
    director_approved_by    VARCHAR(100),
    director_approved_at    TIMESTAMP,
    director_notes          TEXT,

    -- Step 3: Completion (asset closed) — HL-03
    completed_by            VARCHAR(100),
    completed_at            TIMESTAMP,
    final_disposal_value    NUMERIC(18, 2), -- actual value realised from disposal

    -- Rejection (any step)
    rejected_by             VARCHAR(100),
    rejected_at             TIMESTAMP,
    rejection_reason        TEXT,

    -- HL-03: Generated document
    document_ref            VARCHAR(255),   -- Bien ban thanh ly document number
    document_generated_at   TIMESTAMP,
    document_signed         BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Audit fields
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100)    NOT NULL
);

-- Indexes
CREATE INDEX idx_handover_code      ON handover_requests(request_code);
CREATE INDEX idx_handover_asset     ON handover_requests(asset_id);
CREATE INDEX idx_handover_status    ON handover_requests(status);
CREATE INDEX idx_handover_from_unit ON handover_requests(from_unit_id);
CREATE INDEX idx_handover_to_unit   ON handover_requests(to_unit_id);
CREATE INDEX idx_liquidation_code   ON liquidation_requests(request_code);
CREATE INDEX idx_liquidation_asset  ON liquidation_requests(asset_id);
CREATE INDEX idx_liquidation_status ON liquidation_requests(status);
CREATE INDEX idx_liquidation_unit   ON liquidation_requests(requesting_unit_id);

-- Note for Linh (M4):
-- When a liquidation is COMPLETED:
--   1. Update assets SET status = 'LIQUIDATED', status_changed_at = NOW()
--   2. Write a row to asset_history with event_type = 'LIQUIDATED'
--   3. Write a row to audit_log (V5)
-- This ensures HL-03 (post-liquidation asset closure) is enforced.
--
-- Separation of duties rule:
--   initiated_by != dept_approved_by (enforced in HandoverService.java)
--   initiated_by != manager_approved_by (enforced in LiquidationService.java)