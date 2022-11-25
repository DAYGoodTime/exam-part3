package com.day.examp3.config;

import com.day.examp3.filter.AdminInterceptor;
import com.day.examp3.filter.NormalInterceptor;
import com.day.examp3.filter.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SpringConfig implements WebMvcConfigurer {

    @Autowired
    RestTemplateBuilder builder;
    @Bean
    public RestTemplate restTemplate(){
        return builder.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/user/**").addPathPatterns("/addToCart");
        registry.addInterceptor(new AdminInterceptor()).addPathPatterns("/admin/**").excludePathPatterns("/admin/login");
        registry.addInterceptor(new NormalInterceptor()).addPathPatterns("/**").excludePathPatterns("/user/logout");
    }





}