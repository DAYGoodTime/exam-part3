package com.day.examp3.utils;

import java.lang.annotation.*;

/**
 * 配置支持刷新配置的配置类注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshValue {
}