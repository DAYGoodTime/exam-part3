package com.day.examp3.services;


import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.pojo.User;

public interface UserServices {
    /**
     * 根据account类型匹配面获取需要登录的用户
     * @param account 不同形式的账号
     * @param password 密码
     * @return 用户对象
     */
    User getLoginUser(String account,String password);

    /**
     * 注册一个用户
     * @param jsonObject json格式的用户数据
     * @return 是否注册成功
     */
    boolean registerAnUser(JSONObject jsonObject);

    /**
     * 添加到用户购物车列表
     * @param user_id 用户ID
     * @param product_id 产品ID
     * @return 是否添加成功
     */
    boolean addToUserCart(String user_id,String product_id,Integer amount);

    /**
     * 删除该产品在用户的购物车列表
     * @param user_id 用户ID
     * @param product_id 产品ID
     * @return 是否删除成功
     */
    boolean delProductToUserCart(String user_id,String product_id);

    /**
     * 查询该邮箱是否已经存在
     * @param email 需要查询的邮箱
     * @return 是否存在
     */
    boolean isEmailDeduplicate(String email);

}
