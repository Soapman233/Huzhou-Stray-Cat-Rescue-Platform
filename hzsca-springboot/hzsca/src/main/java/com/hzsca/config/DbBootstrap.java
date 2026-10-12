package com.hzsca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库自举：应用启动时确保表结构存在并写入演示数据。
 *
 * <p>执行顺序 {@code @Order(10)}，早于 {@code PasswordMigrationRunner}(@Order(20))，
 * 以便种子数据先落库、再由口令迁移器统一把明文口令升级为 BCrypt。</p>
 */
@Component
@Order(10)
public class DbBootstrap implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DbBootstrap.class);
    private final JdbcTemplate jdbcTemplate;

    public DbBootstrap(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureBaseSchema();
            ensureTables();
            ensureVolunteerTables();
            seedCatTraits();
            seedDemoDataIfNeeded();
            seedAdminAccountIfNeeded();
            ensurePaymentTables();
            if (args != null && args.containsOption("db.bootstrap.only")) {
                List<String> values = args.getOptionValues("db.bootstrap.only");
                if (values != null && !values.isEmpty() && StringUtils.hasText(values.get(0))
                        && Boolean.parseBoolean(values.get(0))) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            logger.warn("DB bootstrap skipped: {}", e.getClass().getSimpleName());
        }
    }

    /**
     * 基础表结构（17 张核心表）。新环境首次启动时自动执行 db/schema.sql 建库，
     * 脚本内部全部使用 CREATE TABLE IF NOT EXISTS，不会覆盖已有数据。
     */
    private void ensureBaseSchema() throws Exception {
        if (tableExists("animals") && tableExists("users") && tableExists("activity") && tableExists("wx_user")) {
            return;
        }
        logger.info("检测到核心表缺失，执行 db/schema.sql 初始化基础表结构");
        executeSqlResource("db/schema.sql");
    }

    /** 支付订单表与支付审计日志表 */
    private void ensurePaymentTables() throws Exception {
        if (!tableExists("payment_order") || !tableExists("payment_audit_log")) {
            executeSqlResource("db/payment.sql");
        }
    }

    /** admins 表为空时创建演示管理员（admin / admin123456，口令为 BCrypt 哈希） */
    private void seedAdminAccountIfNeeded() throws Exception {
        if (!tableExists("admins")) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM admins", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        logger.info("admins 表为空，创建演示管理员账号 admin / admin123456");
        executeSqlResource("db/seed_admin_account.sql");
    }

    private void ensureTables() throws Exception {
        if (tableExists("user_adoption_profile") && tableExists("cat_traits") && tableExists("match_weights")) {
            return;
        }
        executeSqlResource("db/matching.sql");
    }

    private void ensureVolunteerTables() throws Exception {
        executeSqlResource("db/volunteer.sql");
    }

    private void seedCatTraits() throws Exception {
        if (!tableExists("cat_traits")) {
            return;
        }
        executeSqlResource("db/seed_cat_traits.sql");
    }

    private void seedDemoDataIfNeeded() throws Exception {
        if (!tableExists("adoption_request") || !tableExists("rescue_report") || !tableExists("wx_user") || !tableExists("animals")) {
            return;
        }
        Integer adoptionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM adoption_request", Integer.class);
        Integer rescueCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM rescue_report", Integer.class);
        Integer volunteerCount = tableExists("volunteer_application")
                ? jdbcTemplate.queryForObject("SELECT COUNT(*) FROM volunteer_application", Integer.class)
                : null;
        Integer participationCount = tableExists("activity_participation")
                ? jdbcTemplate.queryForObject("SELECT COUNT(*) FROM activity_participation", Integer.class)
                : null;
        boolean need = (adoptionCount == null || adoptionCount == 0)
                || (rescueCount == null || rescueCount == 0)
                || (volunteerCount == null || volunteerCount == 0)
                || (participationCount == null || participationCount == 0);
        if (!need) {
            return;
        }
        executeSqlResource("db/seed_admin_demo_data.sql");
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private void executeSqlResource(String classpathLocation) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        if (!resource.exists()) {
            return;
        }
        String sql = resource.getContentAsString(StandardCharsets.UTF_8);
        List<String> statements = splitStatements(sql);
        for (String stmt : statements) {
            String s = stmt.trim();
            if (s.isEmpty()) {
                continue;
            }
            if (s.regionMatches(true, 0, "USE ", 0, 4)) {
                continue;
            }
            try {
                jdbcTemplate.execute(s);
            } catch (Exception e) {
                logger.warn("DB bootstrap statement skipped: {}", e.getClass().getSimpleName());
            }
        }
    }

    private List<String> splitStatements(String sql) {
        String normalized = sql.replace("\r\n", "\n");
        String[] lines = normalized.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) {
                continue;
            }
            sb.append(line).append('\n');
        }

        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
            } else if (c == '"' && !inSingle) {
                inDouble = !inDouble;
            }
            if (c == ';' && !inSingle && !inDouble) {
                out.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (!current.toString().trim().isEmpty()) {
            out.add(current.toString());
        }
        return out;
    }
}
