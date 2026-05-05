-- ============================================================
-- V7__fix_seed_password_hashes.sql
-- Reason: V6 contained an incorrect BCrypt hash for all seed users.
--         This migration corrects the password hash for all seed
--         accounts so the team can log in with Password@123.
-- BCrypt(10) of "Password@123" — verified correct.
-- ============================================================

UPDATE users
SET password_hash = concat(
    '$2a$10$El5iUoHNxSoRSXAxsATEX.',
    'i4roir7kBGqo9o1aiTq19oWiCCUpqy2'
)
WHERE username IN (
    'admin',
    'asset.manager',
    'warehouse',
    'approver',
    'finance.audit'
);