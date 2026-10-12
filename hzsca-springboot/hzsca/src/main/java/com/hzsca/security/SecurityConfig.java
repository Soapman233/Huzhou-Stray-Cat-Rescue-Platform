package com.hzsca.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/wx/login",
                        "/api/admin/login",
                        "/api/auth/**",
                        // 支付宝回调必须匿名可访问；支付配置供捐助页在登录前读取
                        "/api/payment/alipay/notify",
                        "/api/payment/alipay/return",
                        "/api/payment/config",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 公开的只读接口：猫咪与活动浏览无需登录
                .requestMatchers(HttpMethod.GET, "/api/cats/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/activity/**").permitAll()
                // 管理端接口
                .requestMatchers("/api/admin/**").hasRole("admin")
                // 以下写操作仅限管理员，避免普通用户越权增删改（IDOR）
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("admin")
                .requestMatchers(HttpMethod.POST, "/api/cats/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/cats/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/cats/**").hasRole("admin")
                .requestMatchers(
                        "/api/activity/add",
                        "/api/activity/update",
                        "/api/activity/delete/**",
                        "/api/activity/upload"
                ).hasRole("admin")
                .requestMatchers("/api/adoption-assessments/**").hasRole("admin")
                // 其余业务接口需要登录
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
