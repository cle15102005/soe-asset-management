# Docs

Shared documentation for the SOE Asset Management project. All members contribute.

---

## Files

| File | Purpose | Owner | Status |
|------|---------|-------|--------|
| `api-spec.md` | REST API contract — all endpoints, request/response shapes | M1 leads, all review | **Due: Week 2** |
| `database-schema.md` | Full DB schema with field types and relationships | M1 | Week 1 |
| `deployment-guide.md` | How to deploy to production / staging | M5 | Week 5 |
| `test-plan.md` | Test scenarios per module | M5 | Week 5 |

## Diagrams

| File | Purpose |
|------|---------|
| `diagrams/erd.png` | Entity Relationship Diagram — all tables and their relationships |
| `diagrams/architecture.png` | System architecture overview |
| `diagrams/usecase.png` | Use case diagram per role |

---

## Critical: Week 2 API Agreement

Before anyone writes a frontend page or backend service that crosses module boundaries, the team must agree on:

1. **All endpoint paths and HTTP methods**
2. **All request body shapes**
3. **All response DTO shapes**
4. **Pagination format** (use `PageResponse<T>` from `common/`)
5. **Error response format** (use `ApiResponse<T>` from `common/`)

M4 (frontend) is blocked on types until this is done. Prioritize `api-spec.md` in Week 2.