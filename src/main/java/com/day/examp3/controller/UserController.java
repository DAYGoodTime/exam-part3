package com.day.examp3.controller;

import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.User;
import com.day.examp3.services.UserServicesImpl;
import com.day.examp3.utils.DataUtil;
import com.day.examp3.utils.NanoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@CrossOrigin
public class UserController {

    @Autowired
    UserServicesImpl userServices;


    @Autowired
    UserMapper userMapper;
    /*

     */


    @RequestMapping("/test")
    public String testing(HttpSession session){
        Object userid = session.getAttribute("userid");
        System.out.println(userid);
        return "index";
    }
    @RequestMapping("/user/resetPwd/{uid}")
    public String rest_userPwd(@PathVariable("uid")String user_id,String newPassword){
        User user = new User();
        user.setUserId(user_id);
        user.setPassword(newPassword);
        if(userMapper.updateById(user)>=1){
            return "login";
        }else {
            //TODO 修改密码失败
            return "remima";
        }

    }

    @RequestMapping(value = "/addToCart",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addToCart(@RequestBody JSONObject jsonParam){
        String user_id = jsonParam.getString("user_id");
        String product_id = jsonParam.getString("product_id");
        JSONObject json = new JSONObject();
        String code;
        if(userServices.addToUserCart(user_id,product_id)) code = "200";
        else {
            code = "201";
        }
        json.put("code",code);
        json.put("user_id",user_id);
        json.put("product_id",product_id);
        return json.toJSONString();
    }

    @RequestMapping(value = "/delCart",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String delCart(@RequestBody JSONObject jsonParam){
        String user_id = jsonParam.getString("user_id");
        String product_id = jsonParam.getString("product_id");
        JSONObject json = new JSONObject();
        String code;
        if(userServices.delProductToUserCart(user_id,product_id)) code = "200";
        else {
            code = "201";
        }
        json.put("code",code);
        json.put("user_id",user_id);
        json.put("product_id",product_id);
        return json.toJSONString();
    }





    @RequestMapping("/user/information/{uid}")
    public String user_information(Model model, @PathVariable("uid")String user_id){
        User user = null;
        user = userMapper.selectById(user_id);
        if(user==null){
            //TODO 登录失败操作
            return "mygxin";
        }else{
            model.addAttribute("user",user);
            return "mygrxx";
        }
    }


    @RequestMapping("/user/profile")
    public String user_profile(HttpSession session,Model model){
        String user_id = (String) session.getAttribute("user_id");
        User user = null;
        user = userMapper.selectById(user_id);
        if(user==null){
            //TODO 登录失败操作
        }else{
            model.addAttribute("user",user);
        }

        return "mygxin";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/main";
    }

    @RequestMapping(value = "/user/updateBaseInfo",method = RequestMethod.POST)
    public String updateUserInfo(@RequestParam Map<String,Object> map,String area,HttpSession session) {
        String user_id = (String) session.getAttribute("user_id");
        User oldUser = userMapper.selectById(user_id);
        if(area!=null){
            oldUser.setArea(area);
            userMapper.updateById(oldUser);
            return "redirect:/user/information/"+user_id;
        }
        String username = (String) map.get("username");
        String birth = (String) map.get("birth");
        String sex = (String) map.get("sex");

        oldUser.setNickName(username);
        //日期处理
        Timestamp data = DataUtil.StringToStamp(birth);
        oldUser.setBirth(data);
        oldUser.setSex(sex);
        userMapper.updateById(oldUser);
        return "redirect:/user/information/"+user_id;
    }
    @RequestMapping(value = "/user/updateArea",method = RequestMethod.POST)
    public String UserUpdateArea(String area,HttpSession session){
        return this.updateUserInfo(null,area,session);
    }
    @RequestMapping(value = "/user/updateAvatar",method = RequestMethod.POST)
    @ResponseBody
    public String UserUpdateAvatar(HttpServletRequest request,MultipartFile file,HttpSession session){
        String time = DataUtil.DataToString(new Date());
        String code = "200";
        User user = userMapper.selectById((Serializable) session.getAttribute("user_id"));
        if(user==null) return "201";
        //图片上传服务器后所在的文件夹
        String path = ClassUtils.getDefaultClassLoader().getResource("static/userAvr").getPath();
        System.out.println(path);
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.length() - 4);
        //通常需要修改图片的名字（防止重复）
        String newName = session.getAttribute("user_id") + "-" + NanoIdUtils.randomNanoId()+"-"+time+prefix;
        try {
            //将文件放到目标文件夹
            file.transferTo(new File(path, newName));
            //通常还需要返回图片的URL，为了通用性，需要动态获取协议，不要固定写死
            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/static/userAvr/" + newName;
            System.out.println(returnUrl);
            //上传到数据库中
            user.setHeadPic(returnUrl);
            int res = userMapper.updateById(user);
            if(res!=1) return "201";
            //TODO 头像刷新
            session.setAttribute("user_img",returnUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }


    @RequestMapping( value = "/user/login",method = RequestMethod.POST)
    public String userLogin(String account, String password, String rememberMe,HttpServletRequest request, HttpSession session){
        User user =null;
        user = userServices.getLoginUser(account,password);
        if(user==null){ //登录失败
            //TODO 登录失败操作
            return "/login";
        }else {
            if(rememberMe!= null && rememberMe.equals("true")){
                //TODO 记住我操作
            }
            session.setAttribute("username",user.getNickName());
            session.setAttribute("user_id",user.getUserId());
            session.setAttribute("user_img",user.getHeadPic());
            //TODO 登录时间,登录IP (待完善)
            //登录Ip获取,最基础的方式,分布式ip需要使用其他方法
            String loginIp = request.getRemoteAddr();
            user.setLastLoginIp(loginIp);
            //设置登录时间
            user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
            if(userMapper.updateById(user)>=1){
                //TODO 更新登录时间和IP成功操作
            }else {
                //TODO 更新登录时间和IP失败操作
            }
            return "redirect:/main";
        }

    }

    @RequestMapping(value = "/user/register",method = RequestMethod.POST)
    public String userRegister(String email,String password){
        System.out.println(email);
        if(userServices.registerAnUser(email,password)){
            //如果成功,跳转到登录页面
            return "/login";
        } else{
            //如果失败,跳回注册页面
            return "/reg";
        }
    }
}
