package com.day.examp3.services;

import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.User;
import com.day.examp3.utils.NanoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;

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
    public boolean registerAnUser(String email, String password) {
        //TODO 注册操作可能需要完善
        String defaultName = "用户-"+ NanoIdUtils.randomNanoId();
        User user = new User();
        user.setLoginName(defaultName);
        user.setNickName(defaultName);
        user.setPassword(password);
        user.setEmail(email);
        user.setCardNumber("0"); //默认身份证
        int res = userMapper.insert(user);
        return res >= 1;
    }

    @Override
    public boolean addToUserCart(String user_id, String product_id) {
        return userMapper.addToUserCart(user_id,product_id,new Date())>=1;
    }

    @Override
    public boolean delProductToUserCart(String user_id, String product_id) {
        return userMapper.delShoppingProduct(user_id,product_id)>=1;
    }
}
