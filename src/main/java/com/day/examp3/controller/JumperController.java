package com.day.examp3.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.User;
import com.day.examp3.utils.DataUtil;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import sun.security.provider.MD5;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用于简单的跳转到页面的控制器
 */
@Controller
public class JumperController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RestTemplate restTemplate;

     static final String appid="qm_aI8G7Wqyaz";
     static final String appsec="qms11643D160D6BD090326D21A4A607A854485285E20860";

    @Autowired
    InitializationController initializationController;

    @RequestMapping("/")
    public String toIndex(Model model){
        return initializationController.main(model);
    }

    @RequestMapping("/user/login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/user/register")
    public String toRegister(){
        return "reg";
    }


    @RequestMapping("/user/changePassword")
    public String toChangePwd(){
        return "remima";
    }


    @RequestMapping("/user/myProd")
    public String toMyProd(){
        return "myprod";
    }
    @RequestMapping("/user/address")
    public String toAddress(){
        return "address";
    }






    //TODO 测试支付功能
    @RequestMapping(value = "/test/pay",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String testPay(){
        JSONObject json = new JSONObject();
        //参数
        String amount = "0.01";
        String payfor ="阿健";
        String orderno = "2233";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        StringBuilder sign = new StringBuilder();
        //amount=0.10&appid=qm_fvmZ5vHun7&orderno=120912893872373544&payfor=测试产品&timestamp=20220305214232&appsecret=XXXXXXXX
        sign.append("amount=").append(amount).append("&")
            .append("appid=").append(appid).append("&")
            .append("orderno=").append(orderno).append("&")
            .append("payfor=").append(payfor).append("&")
            .append("timestamp=").append(time).append("&")
            .append("appsecret=").append(appsec);
        String md5sign = SecureUtil.md5(String.valueOf(sign)); //使用了Hutool工具类进行Md5加密
        //HTTP请求
        json.put("amount",amount);
        json.put("appid",appid);
        json.put("orderno",orderno);
        json.put("payfor",payfor);
        json.put("timestamp",time);
        json.put("sign",md5sign);
        String url = "http://pay.shazure.com/payservice/createorder";
//        String url = "http://localhost:8080//test/receive";
        RestTemplate restTemplate = new RestTemplate();
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> entity = new HttpEntity<>(json,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,entity,String.class);
        String response = responseEntity.getBody() ; //{"msg":"调用成功! ", " code" : 1}
        System.out.println("response"+response);
        return JSONObject.parseObject(response).toJSONString();
    }

    @RequestMapping(value = "/test/receive",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getResult(@RequestBody JSONObject jsonObject){
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/order/ok")
    public String toOk(){
        return "ok";
    }
}
