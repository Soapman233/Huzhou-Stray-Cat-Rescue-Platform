package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.WxUser;
import com.hzsca.mapper.WxUserMapper;
import com.hzsca.security.JwtService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wx")
public class WxAuthController {
    private static final Logger logger = LoggerFactory.getLogger(WxAuthController.class);
    private final WxUserMapper wxUserMapper;
    private final JwtService jwtService;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${wechat.miniapp.appid}")
    private String appid;

    @Value("${wechat.miniapp.secret}")
    private String secret;

    public WxAuthController(WxUserMapper wxUserMapper, JwtService jwtService, ObjectMapper objectMapper) {
        this.wxUserMapper = wxUserMapper;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(8000);
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@RequestBody WxLoginRequest request) {
        if (request == null || request.code == null || request.code.isBlank()) {
            return Result.error("400", "缺少 code");
        }
        if (appid == null || appid.isBlank() || secret == null || secret.isBlank()) {
            return Result.error("500", "微信小程序配置缺失");
        }

        WxCode2SessionResponse session;
        try {
            String sessionText = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.weixin.qq.com")
                            .path("/sns/jscode2session")
                            .queryParam("appid", appid)
                            .queryParam("secret", secret)
                            .queryParam("js_code", request.code)
                            .queryParam("grant_type", "authorization_code")
                            .build())
                    .retrieve()
                    .body(String.class);
            session = objectMapper.readValue(sessionText, WxCode2SessionResponse.class);
        } catch (Exception e) {
            String errType = e.getClass().getSimpleName();
            String errMsg = e.getMessage();
            if (errMsg != null && secret != null && !secret.isBlank()) {
                errMsg = errMsg.replace(secret, "***");
            }
            logger.warn("WeChat code2session failed: type={}, message={}", errType, errMsg);
            return Result.error("500", "微信登录失败：" + errType);
        }

        if (session == null) {
            return Result.error("500", "微信登录失败");
        }
        if (session.errcode != null && session.errcode != 0) {
            return Result.error("500", session.errmsg == null ? "微信登录失败" : session.errmsg);
        }
        if (session.openid == null || session.openid.isBlank()) {
            return Result.error("500", "微信登录失败：缺少 openid");
        }

        WxUser user = wxUserMapper.selectByOpenid(session.openid);
        if (user == null) {
            WxUser newUser = new WxUser();
            newUser.setOpenid(session.openid);
            newUser.setUnionid(session.unionid);
            newUser.setNickname(request.nickname);
            newUser.setAvatar(request.avatar);
            newUser.setGender(request.gender);
            newUser.setCity(request.city);
            newUser.setProvince(request.province);
            newUser.setCountry(request.country);
            wxUserMapper.insert(newUser);
            user = wxUserMapper.selectByOpenid(session.openid);
        } else {
            user.setUnionid(session.unionid);
            user.setNickname(request.nickname);
            user.setAvatar(request.avatar);
            user.setGender(request.gender);
            user.setCity(request.city);
            user.setProvince(request.province);
            user.setCountry(request.country);
            wxUserMapper.touchAndUpdateProfile(user);
            user = wxUserMapper.selectByOpenid(session.openid);
        }

        if (user == null || user.getId() == null) {
            return Result.error("500", "用户创建失败");
        }

        String role = user.getRole() == null || user.getRole().isBlank() ? "user" : user.getRole();
        String token = jwtService.generateToken(user.getId(), role, "wx");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", role);
        data.put("user", user);
        return Result.success(data);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WxLoginRequest {
        public String code;
        public String nickname;
        public String avatar;
        public Integer gender;
        public String city;
        public String province;
        public String country;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WxCode2SessionResponse {
        public String openid;
        public String unionid;
        @JsonProperty("session_key")
        public String sessionKey;
        public Integer errcode;
        public String errmsg;
    }
}
