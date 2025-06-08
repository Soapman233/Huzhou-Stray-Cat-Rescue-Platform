package com.hzsca.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源处理器，处理/cats/下的图片请求
        registry.addResourceHandler("/cats/**")
                .addResourceLocations("classpath:/static/cats/")
                .setCachePeriod(3600) // 缓存一小时
                .setUseLastModified(true);

        // 保留通用的静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .setUseLastModified(true);
    }
}