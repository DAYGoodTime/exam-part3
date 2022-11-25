package com.day.examp3.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.dialect.Props;
import com.day.examp3.config.OwnConfig;
import com.day.examp3.utils.AnoValueRefreshPostProcessor;
import com.day.examp3.utils.StatCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.*;

/**
 * 动态修改spring配置文件的工具类
 */
@Component
public class DynamicConfigUtil {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ConfigurableEnvironment environment;
    @Autowired
    OwnConfig ownConfig;
    @Autowired
    StatCode statCode;

    private static final String name = "Config resource 'class path resource [config/application.properties]' via location 'optional:classpath:/config/'";

    /**
     * 更新spring配置
     * @param paramMap k-v参数Map
     * @return 成功状态
     */
    public String updateEnvironment(Map<String,Object> paramMap) {
        Map<String,Object> changedMap = new HashMap<>(paramMap.size());
        MapPropertySource propertySource = (MapPropertySource) environment.getPropertySources().get(name);
        Map<String, Object> source = propertySource.getSource();
        Map<String, Object> map = new HashMap<>(source.size());
        map.putAll(source);
        for (Map.Entry<String,Object> entry:paramMap.entrySet()) {
            if(source.containsKey(entry.getKey())){
                String key = entry.getKey();
                Object value = entry.getValue();
                map.replace(key,value);
                changedMap.put(key,value);
                environment.getPropertySources().replace(name, new MapPropertySource(name, map));
                applicationContext.publishEvent(new AnoValueRefreshPostProcessor.ConfigUpdateEvent(this, key));
            }
        }

        if(!ChangePropertiesFile(changedMap)) return statCode.ErrorCode("修改配置文件失败");
        return statCode.PassCodeOnly("修改配置文件成功");
    }

    /**
     * 非编译状态的配置文件绝对路径
     * (别问为什么要这个,问就是编写状态下无法保存修改过的配置文件,只能同时把工程文件的配置文件也一并修改,但是此文件只能通过绝对路径的方式获取)
     */
    private static final String CodePath = "D:\\DEV\\java\\exam-part3\\src\\main\\resources\\config\\application.properties";

    /**
     * 将被修改的参数重写到配置文件当中去
     * 副作用:这样的配置文件会丢失注释以及格式
     * @param map 需要修改参数的Map
     * @return 是否修改成功
     */
    private boolean ChangePropertiesFile(Map<String,Object> map){
        Props props = new Props("config/application.properties");
        Map<String,String> mapProp = (Map)props;
        for (Map.Entry<String,Object> entry:map.entrySet()) {
            if(mapProp.containsKey(entry.getKey())){
                mapProp.replace(entry.getKey(),String.valueOf(map.get(entry.getKey())));
            }
        }
        Properties newProp = new Properties();
        newProp.putAll(mapProp);
        String path = FileUtil.getAbsolutePath("config/application.properties");
        try {
            newProp.store(FileUtil.getOutputStream(path),null);
            if(ownConfig.isDev()){
                newProp.store(FileUtil.getOutputStream(CodePath),null);
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
