package com.day.expart.controller;

import com.alibaba.fastjson.JSONObject;
import com.day.expart.util.NanoIdUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class MainController {

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping("/getResult")
    @ResponseBody
    public String getResult(
            @RequestParam(value = "orderno")String orderno,
            @RequestParam(value = "status")String status,
            @RequestParam(value = "sign")String sign
    ) throws FileNotFoundException {
        JSONObject jsonObject = new JSONObject();
        System.out.println(orderno);
        System.out.println(status);
        jsonObject.put("orderno",orderno);
        jsonObject.put("status",status);
        jsonObject.put("sign",sign);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String path = "/www/wwwroot/bkend/json";
        System.out.println(path);
        String time = sdf.format(new Date());
        String filename ="[wechatPay]"+NanoIdUtils.randomNanoId()+"-"+time+".json";
        FileOutputStream outputStream = new FileOutputStream(path+"/"+filename);
        OutputStreamWriter osw = new OutputStreamWriter(outputStream,StandardCharsets.UTF_8);
        try {
            osw.write(jsonObject.toString());
            osw.flush();//清空缓冲区，强制输出数据
            osw.close();//关闭输出流
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject json = new JSONObject();
        json.put("orderno",orderno);
        return json.toJSONString();
    }

    @RequestMapping("/getjson")
    @ResponseBody
    public String result(@RequestParam Map<String, Object> params) throws FileNotFoundException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(params);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String path = "/www/wwwroot/bkend/json";
        System.out.println(path);
        String time = sdf.format(new Date());
        String filename ="[ailpay]"+NanoIdUtils.randomNanoId()+"-"+time+".json";
        FileOutputStream outputStream = new FileOutputStream(path+"/"+filename);
        OutputStreamWriter osw = new OutputStreamWriter(outputStream,StandardCharsets.UTF_8);
        try {
            osw.write(jsonObject.toString());
            osw.flush();//清空缓冲区，强制输出数据
            osw.close();//关闭输出流
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toJSONString();
    }
}
