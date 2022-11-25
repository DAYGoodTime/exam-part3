package com.day.examp3.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.pojo.Collection;
import com.day.examp3.servicesImpl.OrderServicesImpl;
import com.day.examp3.servicesImpl.UserServicesImpl;
import com.day.examp3.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

import static com.day.examp3.controller.OrderController.ALPHABET;

@Controller
@CrossOrigin
public class UserController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserServicesImpl userServices;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderServicesImpl orderServices;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    CollectionMapper collectionMapper;

    @Autowired
    StatCode statCode;//状态码工具类


    @RequestMapping("/user/information/{uid}")
    public String user_information(Model model, @PathVariable("uid")String user_id, RedirectAttributes redirectAttributes){
        User user = null;
        user = userMapper.selectById(user_id);
        if(user==null){
            redirectAttributes.addFlashAttribute("msg","登录信息失效,请重新登录");
            return "redirect:/login";
        }else{
            model.addAttribute("user",user);
            return "mygrxx";
        }
    }
    @RequestMapping("/user/profile")
    public String user_profile(HttpSession session,Model model,RedirectAttributes redirectAttributes){
        String user_id = (String) session.getAttribute("user_id");
        User user = null;
        user = userMapper.selectById(user_id);
        if(user==null){
            redirectAttributes.addFlashAttribute("msg","账号信息错误");
            return "redirect:/login";
        }else{
            //查询用户订单数量
            Map<String,Integer> map = orderServices.queryOrderCountsByStatus(user_id);
            model.addAttribute("orderstatucount",map);
            //查询用户收藏商品数量
            model.addAttribute("collectionCount",collectionMapper.queryUserCollectionCount(user_id));
            //信息脱敏
            user.setEmail(DesensitizedUtil.email(user.getEmail()));
            model.addAttribute("user",user);
        }
        return "mygxin";
    }


    @RequestMapping("/user/logout")
    public String logout(HttpSession session,HttpServletResponse response,HttpServletRequest request){
        session.invalidate();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

        }//statCode.PassCodeOnly("ok");
        return "redirect:/main";
    }
    @RequestMapping("/loginReq")
    public String userLogin(String account,String password,String rememberMe,
                            HttpServletResponse response,
                            HttpServletRequest request, HttpSession session,RedirectAttributes redirectAttributes){
        User user =null;
        user = userServices.getLoginUser(account, SecureUtil.md5(password));
        if(user==null){ //登录失败
            redirectAttributes.addFlashAttribute("msg","账号密码错误");
            return "redirect:/login";
        }else {
            Integer numbers = userMapper.getUserShoppingCount(user.getUserId());
            if(rememberMe!= null && rememberMe.equals("true")){
                Cookie userid = new Cookie("user_id",user.getUserId());
                userid.setMaxAge(24*60*60);
                Cookie username = new Cookie("username",user.getNickName());
                username.setMaxAge(24*60*60);
                Cookie user_img = new Cookie("user_img",user.getHeadPic());
                user_img.setMaxAge(24*60*60);
                Cookie user_isAdmin = new Cookie("isAdmin",String.valueOf(user.getIsAdmin()));
                user_isAdmin.setMaxAge(24*60*60);
                Cookie user_shoppings = new Cookie("userShoppings",numbers.toString());
                user_shoppings.setMaxAge(24*60*60);
                response.addCookie(userid);
                response.addCookie(username);
                response.addCookie(user_img);
                response.addCookie(user_isAdmin);
                response.addCookie(user_shoppings);
            }
            session.setAttribute("username",user.getNickName());
            session.setAttribute("user_id",user.getUserId());
            session.setAttribute("user_img",user.getHeadPic());
            session.setAttribute("isAdmin",user.getIsAdmin());
            session.setAttribute("userShoppings",numbers);
            //TODO 登录时间,登录IP (待完善)
            //登录Ip获取,最基础的方式,分布式ip需要使用其他方法
            String loginIp = request.getRemoteAddr();
            user.setLastLoginIp(loginIp);
            //设置登录时间
            user.setLastLoginTime(new Timestamp(new Date().getTime()));
            if(userMapper.updateById(user)>=1){
                //TODO 更新登录时间和IP成功操作
            }else {
                //TODO 更新登录时间和IP失败操作
            }
            return "redirect:/main";
        }
    }
    @RequestMapping(value = "/registerReq",method = RequestMethod.POST)
    @ResponseBody
    public String userRegister(@RequestBody JSONObject jsonObject,HttpSession session){
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String verifyCode = jsonObject.getString("verifyCode");
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("capt");
        if(!shearCaptcha.verify(verifyCode))
            return statCode.ErrorCode("验证码错误");

        if(email==null || password ==null || email.isEmpty() || password.isEmpty())
            return statCode.ErrorCode("密码和邮箱为空");
        if(userServices.isEmailDeduplicate(email)) return statCode.ErrorCode("该邮箱已被注册");
        if(userServices.registerAnUser(jsonObject)){
            //如果成功,跳转到登录页面
            return statCode.PassCodeOnly("操作成功");
        } else{
            //如果失败,跳回注册页面
            return statCode.ErrorCode("注册失败");
        }
    }

    //评价
    @RequestMapping(value = {"/user/myProd/{status}","/user/myProd"})
    public String toMyProd(@PathVariable(value = "status",required = false)String status,Model model,HttpSession session,Boolean isRedirect){
        String user_id = (String) session.getAttribute("user_id");
        List<Order> orders = null;
        if(status==null||status.isEmpty()) {
            orders = orderServices.getUserOrders("已收货", user_id);
            model.addAttribute("currentPage","待评价");
        }
        else if(status.equals("finish")) {
            orders = orderServices.getUserOrders("已完成", user_id);
            model.addAttribute("currentPage","已评价");
        }
        if (orders==null) orders = new ArrayList<>();
        List<Integer> commentAmount = new ArrayList<>();
        for (Order order:orders) {
            commentAmount.add(commentMapper.getCommentAmountByProductId(order.getProduct().getProductId()));
        }
        model.addAttribute("orders",orders);
        model.addAttribute("commentAmount",commentAmount);
        if(isRedirect!=null&&isRedirect) return "redirect:/user/myProd";
        return "myprod";
    }

    @RequestMapping("/user/collection")
    public String userCollection(HttpSession session,Model model){
        String user_id = (String) session.getAttribute("user_id");
        List<Collection> collections = collectionMapper.queryUserCollection(user_id);
        List<Map<String,Object>> models = new ArrayList<>();
        if(!collections.isEmpty()){
            for (Collection coll:collections) {
                Map<String,Object> map = new HashMap<>();
                String pid = coll.getProductId();
                map.put("collection_id",coll.getCollectionId());
                map.put("user_id",coll.getUserId());
                Product product = productMapper.selectById(pid);
                map.put("pid",pid);
                map.put("collection_time",DataUtil.DataToString(new Date(coll.getCreateTime().getTime())));
                map.put("product",product);
                models.add(map);
            }
        }
        model.addAttribute("CollectionList",models);
        return "mycollection";
    }
}
