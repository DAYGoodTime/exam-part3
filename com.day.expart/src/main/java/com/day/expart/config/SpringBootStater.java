package com.day.expart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringBootStater {
    @Bean
    public RestTemplate restTemplate(){     //在SpringBoot启动类中注册RestTemplate
        return new RestTemplate();
    }
}
