package com.day.examp3.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShoppingMapper {

    /**
     * 从用户的购物车上删除该商品
     * @param uid 用户ID
     * @param pid 商品ID
     * @return 操作个数
     */
    @Delete("delete from lmonkey_user_shopping_list where product_id = #{pid} and user_id = #{uid}")
    int delCartByPidAndUid(@Param("uid")String uid,@Param("pid")String pid);

}
