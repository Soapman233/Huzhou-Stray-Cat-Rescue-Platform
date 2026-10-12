package com.hzsca;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用上下文加载测试（集成测试）。
 *
 * <p>该用例会启动完整 Spring 上下文，依赖真实的 MySQL 与 Redis，因此标记为
 * {@code integration} 分组。执行单元测试时可通过以下方式跳过：</p>
 *
 * <pre>
 * # 只跑单元测试（推荐，无需数据库）
 * ./mvnw -o test -Dtest='!HzscaApplicationTests'
 *
 * # 或只跑带 integration 标签的用例
 * ./mvnw -o test -Dgroups=integration
 *
 * # 排除 integration 标签
 * ./mvnw -o test -DexcludedGroups=integration
 * </pre>
 */
@Tag("integration")
@SpringBootTest
class HzscaApplicationTests {

	@Test
	void contextLoads() {
	}

}
