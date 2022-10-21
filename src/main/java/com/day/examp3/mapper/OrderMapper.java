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
    @Select("select * from lmonkey_order where user_id = #{uid} and order_id = #{oid}")
    Order getUserOrderById(@Param("uid")String user_id,@Param("oid")String order_id);

    List<Order> getFullOrdersByStatus(@Param("uid")String user_id,@Param("status")String status);

    @Select("select * from lmonkey_order where user_id = #{uid} and product_id = #{pid}")
    List<Order> getUserOrdersByProId(@Param("pid")String pid,@Param("uid")String user_id);

    List<Order> getFullOrdersByUserId(@Param("uid")String user_id);

}
