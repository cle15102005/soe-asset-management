# Consumable Stock Module — `stock/`

**Owner:** M3
**Feature scope:** CS-01 to CS-04

---

## What This Module Does

Manages consumable materials and depreciable resources used by different departments:
- **CS-01** — Maintain a catalogue of materials with technical specs and units of measure
- **CS-02** — Record stock receipts (materials entering the warehouse)
- **CS-03** — Record stock issues (materials distributed to departments)
- **CS-04** — Track real-time stock balance and departmental usage

---

## Files to Build

| File | What It Does |
|------|-------------|
| `entity/Material.java` | DB table for material catalogue — name, unit, specs, category |
| `entity/StockTransaction.java` | Each receipt or issue event — quantity, date, department, reference doc |
| `entity/TransactionType.java` | Enum: `RECEIPT`, `ISSUE`, `ADJUSTMENT` |
| `repository/MaterialRepository.java` | JPA queries — search by name, category |
| `repository/StockTransactionRepository.java` | Query transactions by material, department, date range |
| `service/MaterialService.java` | CRUD for the material catalogue |
| `service/StockTransactionService.java` | Process receipts and issues; validate stock before issuing |
| `service/StockBalanceService.java` | Compute current balance = total received − total issued |
| `controller/MaterialController.java` | REST endpoints for material catalogue |
| `controller/StockTransactionController.java` | REST endpoints for receipts and issues |
| `dto/MaterialDto.java` | Response shape for material data |
| `dto/StockTransactionDto.java` | Request/response for a transaction |
| `dto/StockBalanceDto.java` | Current stock level per material |
| `dto/DepartmentUsageDto.java` | Summary of how much each department has consumed |

---

## API Endpoints to Implement

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/materials` | List all materials (paginated, filterable) |
| `POST` | `/api/materials` | Add a new material to the catalogue |
| `PUT` | `/api/materials/{id}` | Update material info |
| `GET` | `/api/stock/balance` | Current stock balance for all materials |
| `GET` | `/api/stock/balance/{materialId}` | Balance for a specific material |
| `POST` | `/api/stock/receipt` | Record incoming stock |
| `POST` | `/api/stock/issue` | Record stock issued to a department |
| `GET` | `/api/stock/usage` | Department-wise consumption summary |

> Refer to `docs/api-spec.md` for agreed request/response shapes.

---

## Key Business Rules

- You cannot issue more stock than the current balance — `StockTransactionService` must check this before saving
- Every transaction must record: material, quantity, unit, date, department, and the user who performed it
- `StockBalanceService` should derive balance on-the-fly from transactions — do not store a mutable balance field
- Units of measurement (e.g. kg, litre, piece, box) are stored on the `Material` entity and must be respected in all transaction quantities
- Department field links to `ManagingUnit` from the `user` module — use the ID reference, do not duplicate data

---

## Migration

Your DB tables are defined in: `src/main/resources/db/migration/V3__create_stock.sql`
Coordinate with M1 before adding columns — they manage the migration sequence.

---

## Tests to Write

- `MaterialServiceTest.java` — test create, update, and validation
- `StockTransactionServiceTest.java` — test receipt, issue, and the "insufficient stock" guard