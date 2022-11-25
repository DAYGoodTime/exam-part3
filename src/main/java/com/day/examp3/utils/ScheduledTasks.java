package com.day.examp3.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用于执行计划任务的类
 * (计划任务功能已被注释)
 */
@Component
@EnableScheduling
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    @Autowired
    private BackUpDataBaseManager backUpDataBaseManager;
 
    /**
     * 每天下午4点50分30秒执行
     */
   // 每一分钟执行一次
    //@Scheduled(cron="0 0/1 *  * * ?")
    //@Scheduled(cron = "0 0 0 7,14,21,28 * ? ")
    public void reportCurrentTime() {
        log.info("The time is now {}", DataUtil.getCurrentTimeAsString());
        backUpDataBaseManager.backUpDB();
    }
}