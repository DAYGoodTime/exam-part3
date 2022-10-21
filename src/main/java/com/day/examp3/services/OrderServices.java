package com.day.examp3.services;

import com.day.examp3.pojo.Order;
import com.day.examp3.pojo.Product;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;

/**
 * Order类的服务层
 * 虽然但是,其实也就用来调用比较复杂的查询
 * 简单的查询使用Mapper既可
 */
public interface OrderServices {


    List<Order> getUserFullOrdersByProName(String product_name,String user_id);

    /**
     * 如果status为空查询用户下的所有订单,否则查询用户下对应状态的订单
     * @param status 订单状态
     * @param user_id 用户id
     * @return 订单列表
     */
    List<Order> getUserOrders(String status,String user_id);

}
