package com.example.demo.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnConfig {

    @Value("${spring.thymeleaf.cache}")
    private boolean thymeleafCache;

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize FileUpLoadSize;

}
