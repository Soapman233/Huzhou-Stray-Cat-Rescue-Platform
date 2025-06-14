package com.hzsca.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectPath = System.getProperty("user.dir");
        String uploadPath = "file:" + Paths.get(projectPath, uploadBasePath).toString() + "/";

        // 添加静态资源处理器，处理/cats/下的图片请求
        registry.addResourceHandler("/cats/**")
                .addResourceLocations("classpath:/static/cats/")
                .setCachePeriod(3600) // 缓存一小时
                .setUseLastModified(true);

        // 添加活动图片的资源处理器
        registry.addResourceHandler("/activity/**")
                .addResourceLocations("classpath:/static/activity/")
                .setCachePeriod(3600)
                .setUseLastModified(true);

        // 配置上传文件的访问路径
        registry.addResourceHandler("/" + uploadBasePath + "/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600)
                .resourceChain(true);

        // 添加其他静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}