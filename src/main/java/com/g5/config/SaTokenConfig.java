package com.g5.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
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
                    // 使用 SaRouter 进行更精细的路径控制
//                    SaRouter
//                            // 拦截所有路径
//                            .match("/**")
//                            // 排除 OPTIONS 请求（跨域预检请求）
//                            .notMatch(SaHttpMethod.OPTIONS)
//                            // 对剩余路径进行登录校验
//                            .check(r -> StpUtil.checkLogin());
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
