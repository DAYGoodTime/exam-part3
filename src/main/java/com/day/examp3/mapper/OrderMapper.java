package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Order;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询对应的订单id,以防万一用用户id作为绑定
     * @param user_id 用户订单
     * @param order_id 订单id
     * @return 指定的该用户下的订单id
     */
    Order getUserOrderById(@Param("uid")String user_id,@Param("oid")String order_id);

    /**
     * 查询用户的某个订单状态下的订单数量
     * @param user_id 用户id
     * @param status  订单状态
     * @return 匹配的订单个数
     */
    @Select("select COUNT(*) from lmonkey_order where user_id = #{uid} and status = #{status}")
    int getUserOrderCountsByStatus(@Param("uid")String user_id,@Param("status")String status);

    /**
     * 根据订单状态查询用户下DTO类型的订单
     * @param user_id 用户ID
     * @param status 订单状态
     * @return 匹配的DTO订单集合
     */
    List<Order> getFullOrdersByStatusWithUid(@Param("uid")String user_id,@Param("status")String status);
    /**
     * 根据订单状态查询用户下DTO类型的订单
     * @param status 订单状态
     * @return 匹配的DTO订单集合
     */
    @Select("select * from lmonkey_order where status = #{status} limit #{curPage},#{pageSize}")
    List<Order> getFullOrdersPageByStatus(@Param("status")String status,@Param("curPage") Long curPage,@Param("pageSize")Long pageSize);
    /**
     * 根据订单状态查询用户下DTO类型的订单
     * @param status 订单状态
     * @return 匹配的DTO订单集合
     */
    @Select("select COUNT(*) from lmonkey_order where status = #{status}")
    int getOrdersCountByStatus(@Param("status")String status);

    /**
     * 批量添加订单
     * @param orders 订单集合
     * @return 操作个数
     */
    int addAllOrders(List<Order> orders);

    /**
     * 根据分页查询所有订单
     * @param curPage 当前页数
     * @param pageSize 页数大小
     * @return 匹配的订单集合
     */
    @Select("select * from lmonkey_order limit #{curPage},#{pageSize}")
    List<Order> queryAllOrdersByPage(@Param("curPage") Long curPage,@Param("pageSize")Long pageSize);

    /**
     * 根据分页查询订单,再加上批次ID条件
     * @param curPage 当前页数
     * @param pageSize 页数大小
     * @return 匹配的订单集合
     */
    @Select("select * from lmonkey_order where bound_id = #{bid} limit #{curPage},#{pageSize}")
    List<Order> queryOrdersByPageAndBoundId(@Param("curPage") Long curPage,@Param("pageSize")Long pageSize,@Param("bid")String BoundId);

    /**
     * 根据订单ID查询订单
     * @param oid 订单ID
     * @return 订单对象
     */
    @Select("select * from lmonkey_order where order_id = #{oid}")
    Order queryOrderById(@Param("oid")String oid);

    /**
     * 查询该用户下需要评价的订单
     * @param uid 用户id
     * @param pid 商品id
     * @return 匹配的订单集合
     */
    @Select("select * from lmonkey_order where user_id=#{uid} and product_id=#{pid} and status='已收货'")
    List<Order> queryOrderNeedProd(@Param("uid")String uid,@Param("pid")String pid);

    /**
     * 查询所有订单个数
     * @return 订单个数
     */
    @Select("select count(*) from lmonkey_order")
    int queryAllOrderCounts();

    /**
     * 更新订单中的地址信息
     * @param orders 需要更新的订单集合
     * @return 操作个数
     */
    int updateAllOrdersWithAddId(List<Order> orders);
    /**
     * 更新订单中的支付信息
     * @param orders 需要更新的订单集合
     * @return 操作个数
     */
    int updateAllOrdersWithPayId(List<Order> orders);
    /**
     * 更新订单中的状态信息
     * @param orders 需要更新的订单集合
     * @return 操作个数
     */
    int updateAllOrdersWithStatus(List<Order> orders);

    /**
     * 查询用户下同批次的订单
     * @param user_id 用户ID
     * @param boundId 批次ID
     * @return 匹配的订单集合
     */
    List<Order> getBoundOrderByUserId(@Param("uid")String user_id,@Param("bid")String boundId);

    /**
     * 查询同批次的订单
     * @param boundId 批次ID
     * @return 匹配的订单集合
     */
    List<Order> queryOrdersByBoundId(@Param("bid")String boundId);

    /**
     * 获得用户下某个商品的订单
     * @param user_id 用户ID
     * @param pid 商品ID
     * @return 匹配的订单集合
     */
    List<Order> getUserOrdersByProId(@Param("pid")String pid,@Param("uid")String user_id);

    /**
     * 获得用户下所有的订单
     * @param user_id 用户ID
     * @return 匹配的订单集合
     */
    List<Order> getFullOrdersByUserId(@Param("uid")String user_id);

    /**
     * 获得用户最后5个订单
     * @param user_id 用户ID
     * @return 匹配的订单集合
     */
    List<Order> getLastFiveOrdersByUserId(@Param("uid")String user_id);

}
