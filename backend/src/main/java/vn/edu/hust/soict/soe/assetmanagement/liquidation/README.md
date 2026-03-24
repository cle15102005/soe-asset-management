# Handover · Liquidation · Reporting — `handover/` `liquidation/` `audit/` `report/`

**Owner:** M4
**Feature scope:** HL-01 to HL-04 · RP-01 to RP-03

---

## What This Module Does

### Handover & Liquidation (HL)
Formalizes the transfer and disposal of state-owned assets with a complete approval trail:
- **HL-01** — Submit a handover request for an asset between units
- **HL-02** — Approve or reject a handover; auto-update asset's managing unit on approval
- **HL-03** — Submit a liquidation request for a retired/damaged asset
- **HL-04** — Approve or reject a liquidation; auto-set asset status to `RETIRED`

### Reporting (RP)
Provides management-level visibility across all modules:
- **RP-01** — Fixed asset report: full asset register with current values and status
- **RP-02** — Stock report: balance sheet, receipt/issue history, department usage
- **RP-03** — Audit log: searchable trail of all system actions by user and date

---

## Files to Build

### `handover/`
| File | What It Does |
|------|-------------|
| `entity/HandoverRequest.java` | Asset, from-unit, to-unit, requestor, status, dates |
| `entity/HandoverStatus.java` | Enum: `PENDING`, `APPROVED`, `REJECTED` |
| `repository/HandoverRepository.java` | Query by asset, unit, status |
| `service/HandoverService.java` | Create request, approve/reject logic |
| `service/HandoverDocumentService.java` | Generate formal handover document (PDF or record) |
| `controller/HandoverController.java` | REST endpoints |
| `dto/HandoverDto.java` · `CreateHandoverRequest.java` | Request/response shapes |

### `liquidation/`
| File | What It Does |
|------|-------------|
| `entity/LiquidationRequest.java` | Asset, reason, requestor, status, disposal value |
| `entity/LiquidationStatus.java` | Enum: `PENDING`, `APPROVED`, `REJECTED` |
| `repository/LiquidationRepository.java` | Query by asset, status |
| `service/LiquidationService.java` | Create request, approve/reject, update asset status |
| `controller/LiquidationController.java` | REST endpoints |
| `dto/LiquidationDto.java` · `CreateLiquidationRequest.java` | Request/response shapes |

### `audit/`
| File | What It Does |
|------|-------------|
| `entity/AuditLog.java` | Table: who did what, on which entity, at what time |
| `repository/AuditLogRepository.java` | Query by user, entity type, date range |
| `service/AuditLogService.java` | Read-only service for querying logs |
| `controller/AuditLogController.java` | REST endpoints for audit log search |

> Note: Audit log entries are **written automatically** by Spring Data Envers / `AuditConfig` (set up by M1). You only need to build the read/query side.

### `report/`
| File | What It Does |
|------|-------------|
| `service/AssetReportService.java` | Aggregate asset data for report views |
| `service/StockReportService.java` | Aggregate stock transaction data |
| `service/ExportService.java` | Export report data to Excel (.xlsx) or PDF |
| `controller/ReportController.java` | REST endpoints for report generation |
| `dto/AssetReportDto.java` · `StockReportDto.java` | Report response shapes |

---

## API Endpoints to Implement

### Handover
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/handovers` | List all handover requests |
| `POST` | `/api/handovers` | Submit a new handover request |
| `PUT` | `/api/handovers/{id}/approve` | Approve a request |
| `PUT` | `/api/handovers/{id}/reject` | Reject a request |
| `GET` | `/api/handovers/{id}` | View a specific request |

### Liquidation
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/liquidations` | List all liquidation requests |
| `POST` | `/api/liquidations` | Submit a new liquidation request |
| `PUT` | `/api/liquidations/{id}/approve` | Approve — sets asset to `RETIRED` |
| `PUT` | `/api/liquidations/{id}/reject` | Reject a request |

### Reports & Audit
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/reports/assets` | Full asset register report |
| `GET` | `/api/reports/stock` | Stock balance and usage report |
| `GET` | `/api/reports/assets/export` | Download asset report as Excel/PDF |
| `GET` | `/api/audit-logs` | Query audit log (filter by user, date, entity) |

---

## Key Business Rules

- Only users with appropriate roles (e.g. `MANAGER`, `ADMIN`) can approve/reject — enforce in `SecurityConfig` or with `@PreAuthorize`
- Approving a handover must call into the `asset` module's service to update `managingUnit` — coordinate with M2 on the service method signature
- Approving a liquidation must call into the `asset` module to set status to `RETIRED` — coordinate with M2
- A `PENDING` request blocks further requests on the same asset — validate this before creating new requests
- Reports must support date-range filtering at minimum

---

## Migration

Your DB tables are in:
- `V4__create_handover_liquidation.sql` — handover and liquidation tables
- `V5__create_audit_log.sql` — audit log table

Coordinate with M1 before adding columns.

---

## Tests to Write

- `HandoverServiceTest.java` — test submission, approval, rejection, and asset update side-effect
- `ReportServiceTest.java` — test data aggregation logic and export output