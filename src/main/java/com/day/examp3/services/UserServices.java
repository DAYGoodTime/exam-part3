package com.day.examp3.services;


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
     * 根据邮箱和密码创建并注册一个用户
     * @param email 邮箱地址
     * @param password 密码
     * @return 是否注册成功
     */
    boolean registerAnUser(String email,String password);

    /**
     * 添加到用户购物车列表
     * @param user_id 用户ID
     * @param product_id 产品ID
     * @return 是否添加成功
     */
    boolean addToUserCart(String user_id,String product_id);

    /**
     * 删除该产品在用户的购物车列表
     * @param user_id 用户ID
     * @param product_id 产品ID
     * @return 是否添加成功
     */
    boolean delProductToUserCart(String user_id,String product_id);
}
