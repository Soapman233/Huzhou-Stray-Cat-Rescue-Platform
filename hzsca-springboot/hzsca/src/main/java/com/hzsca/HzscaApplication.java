package com.hzsca;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * HZSCA 流浪猫救助与领养平台 —— 应用入口。
 *
 * <p>{@code @EnableScheduling} 用于开启支付对账等定时任务（见 {@code PaymentReconcileTask}）。</p>
 */
@SpringBootApplication
@MapperScan("com.hzsca.mapper")
@EnableScheduling
public class HzscaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HzscaApplication.class, args);
	}

}
