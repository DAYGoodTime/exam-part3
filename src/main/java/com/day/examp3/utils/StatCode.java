package com.day.examp3.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * 一个简单的请求状态返回的封装类
 * 输出的String为json格式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class StatCode {
    private String code;
    private String msg;
    private Object data;

    public String ErrorCode(String msg){
        StatCode code = new StatCode("201",msg,null);
        return JSONObject.toJSONString(code);
    }
    public String PassCode(String msg,Object data){
        StatCode code = new StatCode("200",msg,data);
        return JSONObject.toJSONString(code);
    }
    public String PassCodeOnly(String msg){
        StatCode code = new StatCode("200",msg,null);
        return JSONObject.toJSONString(code);
    }
}
