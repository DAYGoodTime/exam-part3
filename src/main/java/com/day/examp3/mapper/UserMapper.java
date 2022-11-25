package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号以及密码查询对应的用户
     * @param phone 手机号
     * @param password 密码
     * @return User对象
     */
    @Select("select * from examp3.lmonkey_user where phone = #{phone} and password = #{password}")
    User getLoginUserByPhone(String phone,String password);

    /**
     * 根据电子邮箱以及密码查询对应的用户
     * @param email 电子邮箱
     * @param password 密码
     * @return User对象
     */
    @Select("select * from examp3.lmonkey_user where email = #{email} and password = #{password}")
    User getLoginUserByEmail(String email,String password);

    /**
     * 根据昵称以及密码查询对应的用户
     * @param nick_name 昵称
     * @param password 密码
     * @return User对象
     */
    @Select("select * from examp3.lmonkey_user where nick_name = #{nick_name} and password = #{password}")
    User getLoginUserByNickName(String nick_name,String password);

    /**
     * 获取该用户的所有购物车产品并通过Map-List方式返回
     * @param user_id 用户ID
     * @return Map类型的List
     */
    List<Map<String,Object>> getUserShoppingList(@Param("uid") String user_id);

    /**
     * 删除该用户的产品的购物车列表
     * @param user_id 用户Id
     * @param product_id 产品ID
     * @return 删除数量
     */
    @Delete("delete from lmonkey_user_shopping_list where user_id = #{uid} and product_id = #{pid}")
    int delShoppingProduct(@Param("uid")String user_id,@Param("pid")String product_id);

    /**
     * 添加一个商品到用户的购物车列表
     * @param user_id 用户ID
     * @param product_id 商品ID
     * @param create_time 创建时间
     * @param amount 商品数量
     * @return 操作个数
     */
    @Insert("insert into lmonkey_user_shopping_list (user_id, product_id, create_time,amount) VALUES (#{uid},#{pid},#{c_time},#{amount})")
    int addToUserCart(@Param("uid")String user_id, @Param("pid")String product_id, @Param("c_time")Date create_time,@Param("amount")Integer amount);

    /**
     * 查询用户的购物车数量
     * @param user_id 用户id
     * @return 购物车内的商品数量
     */
    @Select("select COUNT(*) from lmonkey_user_shopping_list where user_id = #{uid}")
    int getUserShoppingCount(@Param("uid")String user_id);

    /**
     * 根据邮箱查询对应的用户,需要保证邮箱地址唯一
     * @param email 唯一邮箱
     * @return 用户对象
     */
    @Select("select * from lmonkey_user where email=#{email}")
    User queryUserByEmail(@Param("email")String email);

}
