-- ============================================================
-- Description: Users, roles, managing units
-- ============================================================

-- Managing units (departments / subsidiary units of the SOE)
CREATE TABLE managing_units (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id   UUID REFERENCES managing_units(id) ON DELETE SET NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100)
);

CREATE TABLE roles (
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

INSERT INTO roles (code, name, description) VALUES
    ('SYSTEM_ADMIN',
     'System Administrator',
     'Manages user accounts, system configuration, and units. Full access to audit logs. No direct write access to operational asset/stock records.'),

    ('ASSET_MANAGER',
     'Asset Manager',
     'Creates and maintains fixed asset profiles, initiates handover and liquidation workflows, tracks asset lifecycle histories within assigned unit.'),

    ('WAREHOUSE',
     'Warehouse / Stock Officer',
     'Manages the consumable materials catalogue, records stock receipts and issues, tracks departmental usage and remaining stock.'),

    ('APPROVING_AUTH',
     'Approving Authority',
     'Approves stock issue requests, handovers, and liquidations. Views executive dashboards and consumption reports filtered to their management scope.'),

    ('FINANCE_AUDIT',
     'Finance & Audit Officer',
     'Read-only access to operational records, depreciation schedules, and audit trails. Generates statutory reports for internal review and external regulatory bodies.');

-- Users
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL, -- bcrypt hash of the user's password
    full_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) UNIQUE,
    phone           VARCHAR(20),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

-- User roles (many-to-many)
CREATE TABLE user_roles (
    user_id     UUID    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id     INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id) -- composite primary key to prevent duplicate role assignments
);

-- User unit assignments
CREATE TABLE user_units (
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    unit_id     UUID NOT NULL REFERENCES managing_units(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, unit_id) -- composite primary key to prevent duplicate unit assignments
);

-- Indexes
CREATE INDEX idx_users_username     ON users(username);
CREATE INDEX idx_users_email        ON users(email);
CREATE INDEX idx_user_roles_user    ON user_roles(user_id);
CREATE INDEX idx_user_units_user    ON user_units(user_id);
CREATE INDEX idx_units_parent       ON managing_units(parent_id);