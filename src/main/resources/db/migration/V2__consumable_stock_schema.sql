-- 1. MATERIALS TABLE (CS-01)
CREATE TABLE materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_code VARCHAR(50) NOT NULL UNIQUE,
    material_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    technical_specs TEXT,
    unit_of_measure VARCHAR(50) NOT NULL,
    supplier VARCHAR(255),
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT materials_code_check CHECK (material_code ~ '^[A-Z0-9_-]+$'),
    CONSTRAINT materials_name_not_empty CHECK (TRIM(material_name) <> '')
);

CREATE INDEX idx_materials_code ON materials(material_code);
CREATE INDEX idx_materials_category ON materials(category);
CREATE INDEX idx_materials_active ON materials(is_active);

-- 2. STOCK_TRANSACTIONS TABLE (CS-02)
CREATE TABLE stock_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_id UUID NOT NULL REFERENCES materials(id) ON DELETE RESTRICT,
    transaction_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    unit VARCHAR(50) NOT NULL,
    transaction_date DATE NOT NULL,
    reference_doc_num VARCHAR(100),
    approving_officer_id UUID,
    notes TEXT,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT stock_trans_type_check CHECK (transaction_type IN ('RECEIPT', 'ISSUE')),
    CONSTRAINT stock_trans_qty_check CHECK (quantity > 0)
);

CREATE INDEX idx_stock_trans_material ON stock_transactions(material_id);
CREATE INDEX idx_stock_trans_date ON stock_transactions(transaction_date);
CREATE INDEX idx_stock_trans_type ON stock_transactions(transaction_type);

-- 3. STOCK_BALANCES TABLE (CS-03)
CREATE TABLE stock_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_id UUID NOT NULL UNIQUE REFERENCES materials(id) ON DELETE CASCADE,
    current_quantity INTEGER NOT NULL DEFAULT 0,
    unit VARCHAR(50) NOT NULL,
    last_receipt_date DATE,
    last_issue_date DATE,
    min_reorder_level INTEGER DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT stock_balance_qty_check CHECK (current_quantity >= 0)
);

CREATE INDEX idx_stock_balance_qty ON stock_balances(current_quantity);
CREATE INDEX idx_stock_balance_material ON stock_balances(material_id);

-- 4. DEPARTMENT_ALLOCATIONS TABLE (CS-04)
CREATE TABLE department_allocations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_transaction_id UUID NOT NULL REFERENCES stock_transactions(id) ON DELETE RESTRICT,
    department_id UUID NOT NULL,
    department_name VARCHAR(255),
    allocated_quantity INTEGER NOT NULL,
    cost_center_code VARCHAR(50),
    budget_period VARCHAR(20),
    allocated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    notes TEXT,
    CONSTRAINT dept_alloc_qty_check CHECK (allocated_quantity > 0)
);

CREATE INDEX idx_dept_alloc_dept ON department_allocations(department_id);
CREATE INDEX idx_dept_alloc_period ON department_allocations(budget_period);
CREATE INDEX idx_dept_alloc_trans ON department_allocations(stock_transaction_id);

-- 5. AUTO-UPDATE STOCK BALANCE
CREATE OR REPLACE FUNCTION update_stock_balance()
RETURNS TRIGGER AS $$
DECLARE
    v_current_qty INTEGER;
    v_material_id UUID;
BEGIN
    v_material_id := NEW.material_id;
    
    SELECT 
        COALESCE(SUM(CASE WHEN transaction_type = 'RECEIPT' THEN quantity ELSE -quantity END), 0)
    INTO v_current_qty
    FROM stock_transactions
    WHERE material_id = v_material_id;
    
    INSERT INTO stock_balances (material_id, current_quantity, unit, updated_at)
    VALUES (v_material_id, GREATEST(0, v_current_qty), NEW.unit, CURRENT_TIMESTAMP)
    ON CONFLICT (material_id) 
    DO UPDATE SET 
        current_quantity = GREATEST(0, v_current_qty),
        unit = NEW.unit,
        last_receipt_date = CASE WHEN NEW.transaction_type = 'RECEIPT' THEN CURRENT_DATE ELSE stock_balances.last_receipt_date END,
        last_issue_date = CASE WHEN NEW.transaction_type = 'ISSUE' THEN CURRENT_DATE ELSE stock_balances.last_issue_date END,
        updated_at = CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_stock_balance_trigger
AFTER INSERT ON stock_transactions
FOR EACH ROW
EXECUTE FUNCTION update_stock_balance();

-- 6. REPORT VIEWS
CREATE OR REPLACE VIEW v_departmental_consumption AS
SELECT 
    da.department_id,
    da.department_name,
    da.budget_period,
    m.material_code,
    m.material_name,
    m.unit_of_measure,
    SUM(da.allocated_quantity) as total_allocated,
    COUNT(da.id) as allocation_count,
    MAX(da.allocated_at) as last_allocation_date
FROM department_allocations da
JOIN stock_transactions st ON st.id = da.stock_transaction_id
JOIN materials m ON m.id = st.material_id
GROUP BY da.department_id, da.department_name, da.budget_period, 
         m.material_code, m.material_name, m.unit_of_measure
ORDER BY da.department_id, da.budget_period DESC, m.material_code;

CREATE OR REPLACE VIEW v_low_stock_alert AS
SELECT 
    m.id,
    m.material_code,
    m.material_name,
    m.unit_of_measure,
    sb.current_quantity,
    sb.min_reorder_level,
    (sb.min_reorder_level - sb.current_quantity) as shortage_qty
FROM materials m
LEFT JOIN stock_balances sb ON sb.material_id = m.id
WHERE m.is_active = TRUE
  AND (sb.current_quantity IS NULL OR sb.current_quantity <= sb.min_reorder_level)
ORDER BY shortage_qty DESC NULLS LAST, m.material_code;
