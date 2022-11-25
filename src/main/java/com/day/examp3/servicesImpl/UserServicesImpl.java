package com.day.examp3.servicesImpl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.Order;
import com.day.examp3.pojo.User;
import com.day.examp3.services.UserServices;
import com.day.examp3.utils.DataUtil;
import com.day.examp3.utils.NanoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class UserServicesImpl implements UserServices {

    @Autowired
    UserMapper userMapper;

    @Override
    public User getLoginUser(String account, String password) {
        //判断account属于昵称 邮箱 还是 手机号
        String regex_phone = "^\\d{11}$"; //手机号匹配
        String regex_name = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$"; //昵称匹配
        String regex_email = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"; //邮箱匹配
        User user = null;
        if(account.matches(regex_phone))
            user = userMapper.getLoginUserByPhone(account,password);
        else if (account.matches(regex_name))
            user = userMapper.getLoginUserByNickName(account,password);
        else if (account.matches(regex_email))
            user = userMapper.getLoginUserByEmail(account, password);
        return user;
    }

    @Override
    public boolean registerAnUser(JSONObject jsonObject) {
        //TODO 注册操作可能需要完善
        String defaultName;
        if( jsonObject.getString("nickname")==null ||jsonObject.getString("nickname").isEmpty()){
            defaultName = "用户-"+ NanoIdUtils.randomNanoId();
        }else {
            defaultName = jsonObject.getString("nickname");
        }
        User user = new User();
        user.setUserId(NanoIdUtils.randomNanoId());
        user.setLoginName(defaultName);
        user.setNickName(defaultName);
        user.setPassword(SecureUtil.md5(jsonObject.getString("password")));
        user.setEmail(jsonObject.getString("email"));
        user.setBirth(DataUtil.StringToStamp(jsonObject.getString("birth")));
        String sex="未知";
        String area="未知";
        if(jsonObject.getString("sex")!=null&&!jsonObject.getString("sex").isEmpty()){
            sex = jsonObject.getString("sex");
        }
        if(jsonObject.getString("area")!=null&&!jsonObject.getString("area").isEmpty()){
            area = jsonObject.getString("area");
        }
        String phone = jsonObject.getString("phone");
        if(phone!=null)
            user.setPhone(phone);
        user.setSex(sex);
        user.setArea(area);
        user.setCardNumber("0"); //默认身份证
        user.setIsAdmin(0); //默认普通用户身份
        int res = userMapper.insert(user);
        return res >= 1;
    }

    @Override
    public boolean addToUserCart(String user_id, String product_id,Integer amount) {
        return userMapper.addToUserCart(user_id,product_id,new Date(),amount)>=1;
    }

    @Override
    public boolean delProductToUserCart(String user_id, String product_id) {
        return userMapper.delShoppingProduct(user_id,product_id)>=1;
    }

    @Override
    public boolean isEmailDeduplicate(String email) {
        User user = userMapper.queryUserByEmail(email);
        return user != null;
    }


}
