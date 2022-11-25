package com.example.demo.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson2.JSONObject;
import com.example.demo.utils.Cos;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.demo.utils.Cos.cosClient;

@Controller
public class controller {

    @RequestMapping("/page")
    public String page(){
        return "page";
    }
    @RequestMapping("/createCapt")
    @ResponseBody
    public String createCapt(HttpServletResponse response,HttpSession session){
        JSONObject jsonObject = new JSONObject();
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(97, 34, 4, 4);
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


    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(HttpServletRequest request) {
        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        MultipartFile file = files.get(0);
        PutObjectResult putObjectResult = Cos.upLoadImgs(file);
        String native_url = Cos.COS_URL + file.getOriginalFilename();
        String encoded_url = URLUtil.encode(native_url);
        System.out.println(encoded_url);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","200");
        jsonObject.put("msg","200");
        jsonObject.put("result",putObjectResult);
        return jsonObject.toJSONString();
    }
}
