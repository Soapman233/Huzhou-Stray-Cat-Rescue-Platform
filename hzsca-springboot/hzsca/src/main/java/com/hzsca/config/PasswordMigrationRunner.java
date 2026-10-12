package com.hzsca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 口令迁移器：启动时把 users / admins 表中残留的**明文口令**统一升级为 BCrypt 哈希。
 *
 * <p>背景：项目早期版本以明文存储用户口令，安全审计后统一改为 BCrypt。为避免历史数据
 * 在用户首次登录前仍以明文落库，这里在应用启动时做一次性迁移。</p>
 *
 * <p>特性：
 * <ul>
 *   <li><b>幂等</b>：已是 BCrypt 形态（{@code $2a$/$2b$/$2y$} 前缀）的记录直接跳过，可重复执行；</li>
 *   <li><b>可关闭</b>：设置 {@code security.password-migration.enabled=false} 可禁用；</li>
 *   <li><b>失败不阻断启动</b>：任一条迁移失败仅告警，不影响应用启动。</li>
 * </ul>
 *
 * <p>执行顺序在 {@link DbBootstrap} 之后（{@code @Order(20)} > {@code @Order(10)}），
 * 确保种子数据先落库、再统一迁移。</p>
 */
@Component
@Order(20)
public class PasswordMigrationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            int userMigrated = migrate("users", "UserID", "Password");
            int adminMigrated = migrate("admins", "id", "password");
            if (userMigrated + adminMigrated > 0) {
                logger.info("口令迁移完成：users {} 条，admins {} 条已由明文升级为 BCrypt", userMigrated, adminMigrated);
            } else {
                logger.info("口令迁移检查完成：未发现需要迁移的明文口令");
            }
        } catch (Exception e) {
            logger.warn("口令迁移跳过：{}", e.getClass().getSimpleName());
        }
    }

    /**
     * 扫描单张表并迁移明文口令。
     *
     * @param table     表名
     * @param idColumn  主键列名
     * @param pwdColumn 口令列名
     * @return 实际迁移的记录数
     */
    private int migrate(String table, String idColumn, String pwdColumn) {
        if (!tableExists(table)) {
            return 0;
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT " + idColumn + " AS id, " + pwdColumn + " AS pwd FROM " + table
                        + " WHERE " + pwdColumn + " IS NOT NULL AND " + pwdColumn + " <> ''");
        int migrated = 0;
        for (Map<String, Object> row : rows) {
            Object raw = row.get("pwd");
            if (raw == null) {
                continue;
            }
            String stored = raw.toString();
            if (isBcrypt(stored)) {
                continue;
            }
            Object id = row.get("id");
            if (id == null) {
                continue;
            }
            try {
                String hashed = passwordEncoder.encode(stored);
                jdbcTemplate.update(
                        "UPDATE " + table + " SET " + pwdColumn + " = ? WHERE " + idColumn + " = ?",
                        hashed, id);
                migrated++;
            } catch (Exception e) {
                logger.warn("口令迁移失败：table={}, id={}, error={}", table, id, e.getClass().getSimpleName());
            }
        }
        return migrated;
    }

    private boolean isBcrypt(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, tableName);
        return count != null && count > 0;
    }
}
