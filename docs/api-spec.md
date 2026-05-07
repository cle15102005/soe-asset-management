# API Contract — SOE Asset Management System
# Version: 1.0 | Base URL: http://localhost:8080/api
#
# CONVENTIONS
# ─────────────────────────────────────────────────────────────
# All responses are wrapped in ApiResponse:
#   { "success": true/false, "message": "...", "data": { ... } }
#
# Authentication: Bearer token in Authorization header
#   Authorization: Bearer <jwt_token>
#
# Dates: ISO 8601 → "2024-01-15"
# Money: integer VND → 25000000 (no decimals in JSON)
# UUIDs: lowercase with dashes → "10000000-0000-0000-0000-000000000001"
#
# HTTP status codes:
#   200 OK           -> success (GET, PATCH, PUT)
#   201 Created      -> success (POST that creates a resource)
#   400 Bad Request  -> validation error or business rule violation
#   401 Unauthorized -> missing or invalid JWT token
#   403 Forbidden    -> authenticated but wrong role
#   404 Not Found    -> resource does not exist
#   500 Server Error -> unexpected error
# ─────────────────────────────────────────────────────────────

---

### POST /api/auth/login
**Description:** Authenticate with username and password. Returns JWT token.
**Auth required:** No (public endpoint)

**Request body:**
```json
{
  "username": "admin",
  "password": "Password@123"
}
```

**Response 200:**
```json
{
  "success": true,
  "message": "Đăng nhập thành công.",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin"
  }
}
```

**Response 400 (wrong password):**
```json
{
  "success": false,
  "message": "Bad credentials"
}
```

---

### GET /api/users/me
**Description:** Get the currently authenticated user's profile.
**Auth required:** Yes — any role

**Response 200:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": "10000000-0000-0000-0000-000000000001",
    "username": "admin",
    "fullName": "Lê Việt Cường",
    "email": "admin@soe.vn",
    "phone": null,
    "isActive": true,
    "roles": ["SYSTEM_ADMIN"],
    "managingUnitCodes": ["HQ"]
  }
}
```

---

### GET /api/users
**Description:** List all users in the system.
**Auth required:** Yes — SYSTEM_ADMIN only

**Response 200:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": "10000000-0000-0000-0000-000000000001",
      "username": "admin",
      "fullName": "Lê Việt Cường",
      "email": "admin@soe.vn",
      "phone": null,
      "isActive": true,
      "roles": ["SYSTEM_ADMIN"],
      "managingUnitCodes": ["HQ"]
    }
  ]
}
```

---

### GET /api/users/{id}
**Description:** Get a single user by UUID.
**Auth required:** Yes — SYSTEM_ADMIN only

**Path param:** `id` — UUID of the user

**Response 200:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": "10000000-0000-0000-0000-000000000002",
    "username": "asset.manager",
    "fullName": "Đinh Hà Hải",
    "email": "asset.manager@soe.vn",
    "phone": null,
    "isActive": true,
    "roles": ["ASSET_MANAGER"],
    "managingUnitCodes": ["PHKT"]
  }
}
```

**Response 404:**
```json
{
  "success": false,
  "message": "Không tìm thấy người dùng với id: <id>"
}
```

---

### POST /api/users
**Description:** Create a new user account.
**Auth required:** Yes — SYSTEM_ADMIN only

**Request body:**
```json
{
  "username": "new.user",
  "password": "Password@123",
  "fullName": "Nguyễn Văn A",
  "email": "new.user@soe.vn",
  "phone": "0901234567",
  "roleCode": "ASSET_MANAGER"
}
```

**Role codes:** `SYSTEM_ADMIN` | `ASSET_MANAGER` | `WAREHOUSE` | `APPROVING_AUTH` | `FINANCE_AUDIT`

**Response 201:**
```json
{
  "success": true,
  "message": "Tạo người dùng thành công.",
  "data": {
    "id": "uuid-of-new-user",
    "username": "new.user",
    "fullName": "Nguyễn Văn A",
    "email": "new.user@soe.vn",
    "phone": "0901234567",
    "isActive": true,
    "roles": ["ASSET_MANAGER"],
    "managingUnitCodes": []
  }
}
```

**Response 400 (duplicate username):**
```json
{
  "success": false,
  "message": "Tên đăng nhập đã tồn tại: new.user"
}
```

---

### PATCH /api/users/{id}/deactivate
**Description:** Deactivate a user account. The user can no longer log in.
**Auth required:** Yes — SYSTEM_ADMIN only

**Path param:** `id` — UUID of the user

**Response 200:**
```json
{
  "success": true,
  "message": "Tài khoản đã bị vô hiệu hóa."
}
```

---

## MODULE: Fixed Assets
# TODO: Fills in FA-01 to FA-04 endpoints here
# Expected endpoints:
#   GET    /api/assets              -> list all assets (paginated, filterable)
#   POST   /api/assets              -> create new asset
#   GET    /api/assets/{id}         -> get asset detail
#   PUT    /api/assets/{id}         -> update asset
#   PATCH  /api/assets/{id}/status  -> update operational status
#   GET    /api/assets/{id}/depreciation -> get depreciation values
#   GET    /api/assets/{id}/history -> get lifecycle history

---

## MODULE: Consumable Stock
# TODO: Huy fills in CS-01 to CS-04 endpoints here
# Expected endpoints:
#   GET    /api/materials            -> list material catalogue
#   POST   /api/materials            -> create material
#   PUT    /api/materials/{id}       -> update material
#   POST   /api/stock/receipt        -> record stock-in transaction
#   POST   /api/stock/issue          -> record stock-out to department
#   GET    /api/stock/balance        -> real-time balance per material
#   GET    /api/stock/usage          -> dept consumption per period

---

## MODULE: Handover, Liquidation & Reporting
# TODO: Linh fills in HL-01 to HL-03 and RP-01 to RP-03 endpoints here
# Expected endpoints:
#   POST   /api/handovers                    -> initiate handover
#   GET    /api/handovers                    -> list handover requests
#   GET    /api/handovers/{id}               -> get handover detail
#   PATCH  /api/handovers/{id}/approve       -> approving authority approves
#   PATCH  /api/handovers/{id}/confirm       -> receiving unit confirms
#   PATCH  /api/handovers/{id}/reject        -> reject handover
#   GET    /api/handovers/{id}/document      -> download PDF record
#   POST   /api/liquidations                 -> submit liquidation request
#   GET    /api/liquidations                 -> list liquidation requests
#   GET    /api/liquidations/{id}            -> get liquidation detail
#   PATCH  /api/liquidations/{id}/approve    -> approve liquidation
#   PATCH  /api/liquidations/{id}/reject     -> reject liquidation
#   GET    /api/audit-logs                   -> paginated audit log
#   GET    /api/reports/assets               -> asset inventory report
#   GET    /api/reports/assets/export        -> export Excel/PDF
#   GET    /api/reports/stock                -> stock consumption report
#   GET    /api/reports/stock/export         -> export Excel

---

## SHARED: Common response shapes

### Validation error (400)
```json
{
  "success": false,
  "message": "Dữ liệu không hợp lệ.",
  "data": {
    "username": "Tên đăng nhập không được để trống.",
    "password": "Mật khẩu phải có ít nhất 8 ký tự."
  }
}
```

### Unauthorized (401)
```json
{
  "success": false,
  "message": "Unauthorized"
}
```

### Forbidden (403)
```json
{
  "success": false,
  "message": "Bạn không có quyền thực hiện thao tác này."
}
```

### Not found (404)
```json
{
  "success": false,
  "message": "Không tìm thấy tài nguyên với id: <id>"
}
```

---

## MOCK DATA for frontend
# Use these while waiting for real backends to be ready.
# Shape matches exactly what the real API will return.

### Mock user (after login)
```json
{
  "id": "10000000-0000-0000-0000-000000000001",
  "username": "admin",
  "fullName": "Lê Việt Cường",
  "email": "admin@soe.vn",
  "isActive": true,
  "roles": ["SYSTEM_ADMIN"],
  "managingUnitCodes": ["HQ"]
}
```

### Mock asset list item
```json
{
  "id": "30000000-0000-0000-0000-000000000001",
  "assetCode": "TS-2024-001",
  "name": "Máy tính xách tay Dell Latitude 5540",
  "category": "Thiết bị công nghệ thông tin",
  "managingUnit": "Phòng Hành chính - Kỹ thuật",
  "originalCost": 25000000,
  "accumulatedDepreciation": 5000000,
  "netBookValue": 20000000,
  "acquisitionDate": "2024-01-15",
  "status": "IN_USE"
}
```

### Mock material list item
```json
{
  "id": "40000000-0000-0000-0000-000000000001",
  "materialCode": "VT-2024-001",
  "name": "Giấy A4 IK Premium 70gsm",
  "category": "Văn phòng phẩm",
  "unitOfMeasure": "Ream",
  "unitPrice": 85000,
  "currentBalance": 45.000
}
```

### Asset status labels (Vietnamese)
```
IN_USE       -> Đang sử dụng
MAINTENANCE  -> Đang bảo trì
IDLE         -> Chờ phân bổ
TRANSFERRED  -> Đã bàn giao
LIQUIDATED   -> Đã thanh lý
```

### Handover status labels (Vietnamese)
```
DRAFT            -> Nháp
PENDING_APPROVAL -> Chờ phê duyệt
APPROVED         -> Đã phê duyệt
CONFIRMED        -> Đã xác nhận
COMPLETED        -> Hoàn thành
REJECTED         -> Đã từ chối
```

### Liquidation status labels (Vietnamese)
```
DRAFT            -> Nháp
PENDING_MANAGER  -> Chờ quản lý
PENDING_DIRECTOR -> Chờ giám đốc
APPROVED         -> Đã phê duyệt
COMPLETED        -> Hoàn thành
REJECTED         -> Đã từ chối
```