package com.day.examp3.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.day.examp3.pojo.Order;
import com.day.examp3.pojo.Product;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;
import java.util.Map;

/**
 * Order类的服务层
 * 虽然但是,其实也就用来调用比较复杂的查询
 * 简单的查询使用Mapper既可
 */
public interface OrderServices {


    List<Order> getUserFullOrdersByProName(String product_name,String user_id);

    /**
     * 如果status为空则查询用户下的所有订单,否则查询用户下对应状态的订单
     * @param status 订单状态
     * @param user_id 用户id
     * @return 订单列表
     */
    List<Order> getUserOrders(String status,String user_id);

    /**
     * 封装Page插件
     * 查询对应status的订单,为空则全部查询
     * @param status 订单状态
     * @return 订单列表
     */
    Page<Order> getOrdersPageByStatus(Page<Order> page,String status);

    /**
     * 上传一批的订单
     * @param orders 订单列表
     * @return 是否全部上传成功
     */
    boolean addBatchOrders(List<Order> orders);

    /**
     * 更新同一批次订单的收件地址
     * @param orders 被修改过收件地址的订单
     * @return 是否成功修改
     */
    boolean updateBatchOrdersWithAdd(List<Order> orders);

    /**
     * 更新同一批次订单的支付方式
     * @param orders 被修改过支付方式的订单
     * @return 是否成功修改
     */
    boolean updateBatchOrdersWithPayment(List<Order> orders);

    /**
     * 更新同一批次订单的状态
     * @param orders 被修改过状态的订单
     * @return 是否成功修改
     */
    boolean updateBatchOrdersWithStatus(List<Order> orders);
    /**
     *封装Page插件
     * @param page
     * @return
     */
    Page<Order> queryAllOrdersPage(Page<Order> page);
    /**
     * 蛋疼的封装Page插件并可以用于搜索对应的批次ID
     * @param page
     * @param BoundId
     * @return
     */
    Page<Order> queryOrdersPageByBoundId(Page<Order> page,String BoundId);

    /**
     * 根据用户查询用户下各个订单状态的数量
     * @param uid 用户id
     * @return map集合,对应状态的数量
     */
    Map<String,Integer> queryOrderCountsByStatus(String uid);

}
