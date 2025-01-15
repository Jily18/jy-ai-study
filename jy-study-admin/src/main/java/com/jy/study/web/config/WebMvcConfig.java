package com.jy.study.web.config;

import com.jy.study.framework.interceptor.WebLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private WebLoginInterceptor webLoginInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webLoginInterceptor)
                .addPathPatterns("/web/**")
                .excludePathPatterns("/web/login", "/web/register", "/web/captcha/**");
    }
} 