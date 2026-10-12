-- ============================================================
-- HZSCA 演示管理员账号（种子数据）
-- ------------------------------------------------------------
-- 说明：新环境首次启动时若 admins 表为空，可用本脚本创建演示管理员。
--       口令以 BCrypt 哈希存储，**不存明文**。
--
--   演示账号：admin / admin123456
--   ⚠️ 仅用于本地演示，请勿用于生产环境；上线前务必修改口令。
--
-- 手动执行：mysql -uroot -p hzsca < seed_admin_account.sql
-- ============================================================

USE hzsca;

-- 口令 admin123456 的 BCrypt 哈希
INSERT IGNORE INTO `admins` (`id`, `username`, `password`, `name`, `role`, `avatar`)
VALUES (1, 'admin', '$2b$10$ZH3CUU7EFea7uv1rj85KrOSfeTkWfWTTnnxAyB16fKc4pULQ1ggVm', '管理员', 'admin', '');
