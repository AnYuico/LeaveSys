package com.g5.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，定义需要登录才能访问的接口路径
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 校验登录状态，未登录会抛出异常
                    StpUtil.checkLogin();
                }))
                .addPathPatterns("/**")                // 拦截所有请求
                .excludePathPatterns(                  // 排除登录和静态资源
                        "/user/login",
                        "/user/register",
                        "/logout",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/doc.html"
                );
    }
}
