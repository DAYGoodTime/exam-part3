package com.day.examp3.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.CategoryMapper;
import com.day.examp3.mapper.LocationMapper;
import com.day.examp3.pojo.Location;
import com.day.examp3.servicesImpl.CategoryServicesImpl;
import com.day.examp3.utils.StatCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 其他不好分类的Controller层
 */
@Controller
public class OtherController {
    @Autowired
    LocationMapper locationMapper;

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    CategoryServicesImpl categoryServices;

    @Autowired
    StatCode statCode;


    @PostMapping("/getLocation/{pid}")
    @ResponseBody
    public String getCities(@PathVariable("pid")Integer pid){
        List<Location> locals = locationMapper.getLocations(pid);
        JSONObject json = new JSONObject();
        json.put("cities",locals);
        return json.toJSONString();
    }

    //用于创建验证码
    @RequestMapping("/createCapt")
    @ResponseBody
    public String createCapt(HttpServletResponse response, HttpSession session){
        JSONObject jsonObject = new JSONObject();
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(120, 50, 4, 4);
        ServletOutputStream outputStream;
        try {
            outputStream  = response.getOutputStream();
            captcha.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            jsonObject.put("status","201");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status","200");
        session.setAttribute("capt",captcha);
        return jsonObject.toJSONString();
    }
    //用于验证验证码的测试接口
    @RequestMapping("/vertify")
    @ResponseBody
    public String vertify(HttpSession session, String code){
        ShearCaptcha capt = (ShearCaptcha) session.getAttribute("capt");
        JSONObject jsonObject = new JSONObject();
        System.out.println(code);
        if(capt!=null && capt.verify(code)){
            jsonObject.put("code","200");
        }else{
            jsonObject.put("code","201");
        }
        jsonObject.put("msg","msg");
        return jsonObject.toJSONString();
    }

    @RequestMapping("/queryTopBar")
    @ResponseBody
    public String queryTop() {
        //首先查询所有的大分类
        List<Map<String, String>> MainCategory = categoryServices.queryAllFatherCategory();
        //有子分类的主分类
        List<JSONObject> hasChildrenCategory = categoryServices.queryAllFatherWithChildren();
        //没有子分类的主分类
        List<Map<String, String>> noChildrenCategory = categoryMapper.queryAllFatherCategoryWithNoChildren();
        JSONObject json = new JSONObject();
        json.put("MainCategory", MainCategory);
        json.put("hasChildrenCategory", hasChildrenCategory);
        json.put("noChildrenCategory", noChildrenCategory);
        return statCode.PassCode("操作成功", json);
    }
}
