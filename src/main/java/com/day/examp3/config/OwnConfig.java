package com.day.examp3.config;


import com.day.examp3.utils.RefreshValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@RefreshValue
public class OwnConfig {

    @Value("${spring.thymeleaf.cache}")
    private boolean thymeleafCache;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String FileUpLoadSize;

    @Value("${Dev:false}")
    private boolean Dev;
}

