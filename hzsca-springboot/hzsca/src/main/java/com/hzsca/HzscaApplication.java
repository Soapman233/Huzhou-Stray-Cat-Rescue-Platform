package com.hzsca;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hzsca.mapper")
public class HzscaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HzscaApplication.class, args);
	}

}
