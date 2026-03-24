# Foundation & Auth — `config/` `common/` `exception/` `auth/` `user/`

**Owner:** M1 — Project Manager
**This is the foundation all other modules are built on. Complete this first.**

---

## What M1 Builds

| Area | Purpose |
|------|---------|
| **Security & JWT** | Authenticate every request; issue and validate JWT tokens |
| **RBAC** | Role-based access — `ADMIN`, `MANAGER`, `STAFF`, `VIEWER` |
| **Shared infrastructure** | `BaseEntity`, `ApiResponse`, `PageResponse` used by all modules |
| **Exception handling** | Global error handler so all errors return consistent JSON |
| **User management** | CRUD for system users and their roles |
| **DB migration foundation** | V1 migration and Flyway setup |

---

## Delivery Order

M1 must deliver these before other members can start their modules:

1. `BaseEntity.java` — all other entities extend this
2. `ApiResponse.java` + `PageResponse.java` — all controllers use these
3. `GlobalExceptionHandler.java` — all exception classes must be registered here
4. `SecurityConfig.java` — defines which roles can access which URL patterns
5. `V1__create_users_roles.sql` + `V6__seed_data.sql` — so others can run the app locally
6. `application-dev.yml` — local dev database config

---

## `config/`

| File | Sets Up |
|------|---------|
| `SecurityConfig.java` | Spring Security filter chain, URL access rules per role |
| `JwtConfig.java` | JWT secret, expiry, signing algorithm |
| `CorsConfig.java` | Allows frontend (`localhost:5173`) to call the API |
| `AuditConfig.java` | Spring Data Envers — automatically logs entity changes to `audit_log` |

---

## `common/`

| File | Used By |
|------|---------|
| `BaseEntity.java` | All `@Entity` classes — provides `id`, `createdAt`, `updatedAt`, `createdBy` |
| `ApiResponse.java` | All controllers — wrap every response: `{ success, message, data }` |
| `PageResponse.java` | All list endpoints — wrap paginated data: `{ content, page, size, total }` |
| `GlobalExceptionHandler.java` | Catches all thrown exceptions and returns consistent error JSON |

---

## `exception/`

| File | Thrown When |
|------|-------------|
| `ResourceNotFoundException.java` | Entity with given ID does not exist → HTTP 404 |
| `AccessDeniedException.java` | User lacks permission → HTTP 403 |
| `BusinessRuleException.java` | Domain rule violated (e.g. issuing stock below zero) → HTTP 422 |

All module owners throw these — never return raw 500 errors.

---

## `auth/`

| File | Does |
|------|------|
| `AuthController.java` | `POST /api/auth/login` — validates credentials, returns JWT |
| `AuthService.java` | Looks up user, checks password, builds JWT claims |
| `JwtService.java` | Generate, sign, and validate JWT tokens |
| `JwtAuthFilter.java` | Spring filter — extracts JWT from `Authorization` header on every request |

---

## `user/`

| File | Does |
|------|------|
| `User.java` | DB entity — username, password hash, role, managing unit |
| `Role.java` | Enum: `ADMIN`, `MANAGER`, `STAFF`, `VIEWER` |
| `ManagingUnit.java` | DB entity — department/unit that owns assets and consumes stock |
| `UserController.java` | `GET/POST/PUT /api/users` — admin only |
| `UserService.java` | User CRUD with password hashing (BCrypt) |
| `UserRepository.java` | `findByUsername()` needed by `AuthService` |