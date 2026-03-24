# Fixed Assets Module — `asset/`

**Owner:** M2
**Feature scope:** FA-01 to FA-04

---

## What This Module Does

Manages the full lifecycle of fixed assets and machinery owned by the SOE:
- **FA-01** — Register a new fixed asset with full technical profile
- **FA-02** — Update asset details and track status changes (in-use, under-repair, retired)
- **FA-03** — Calculate and record periodic depreciation
- **FA-04** — View asset history and audit trail

---

## Files to Build

| File | What It Does |
|------|-------------|
| `entity/Asset.java` | DB table for fixed assets — managing unit, technical specs, historical cost, status |
| `entity/AssetHistory.java` | Records every status/value change on an asset |
| `entity/AssetStatus.java` | Enum: `IN_USE`, `UNDER_REPAIR`, `RETIRED`, `TRANSFERRED` |
| `repository/AssetRepository.java` | JPA queries — search by unit, status, category |
| `repository/AssetHistoryRepository.java` | Query history records by asset ID |
| `service/AssetService.java` | Create, update, search, status-change logic |
| `service/DepreciationService.java` | Straight-line depreciation calculation; saves result to asset |
| `controller/AssetController.java` | REST endpoints (see below) |
| `dto/AssetDto.java` | Response shape for asset data |
| `dto/CreateAssetRequest.java` | Payload for creating a new asset |
| `dto/UpdateAssetRequest.java` | Payload for editing an asset |
| `dto/DepreciationDto.java` | Response shape for depreciation calculation results |

---

## API Endpoints to Implement

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/assets` | List all assets (paginated, filterable by status/unit) |
| `GET` | `/api/assets/{id}` | Get a single asset detail |
| `POST` | `/api/assets` | Register a new asset |
| `PUT` | `/api/assets/{id}` | Update asset info or status |
| `GET` | `/api/assets/{id}/history` | Get change history for an asset |
| `POST` | `/api/assets/{id}/depreciation` | Calculate & record depreciation for this period |

> Refer to `docs/api-spec.md` for agreed request/response shapes.

---

## Key Business Rules

- An asset must always belong to a `ManagingUnit` (from the `user` module)
- Historical cost and purchase date are required at creation
- Status transitions must be logged in `AssetHistory` automatically (use a service method, not manual saves in the controller)
- Depreciation: use the straight-line method — `(historical_cost - salvage_value) / useful_life_years`
- A retired asset cannot be updated

---

## Migration

Your DB table is defined in: `src/main/resources/db/migration/V2__create_assets.sql`
Coordinate with M1 before adding columns — they manage the migration sequence.

---

## Tests to Write

- `AssetServiceTest.java` — test create, update, status transitions, and validation rules
- `DepreciationServiceTest.java` — test depreciation formula with edge cases (zero salvage value, partial year)