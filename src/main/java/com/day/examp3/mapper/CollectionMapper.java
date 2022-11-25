package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Collection;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 跟收藏操作相关的mapper
 */
@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
    /**
     * 查询该用户是否收藏了该商品
     * @param user_id 用户id
     * @param product_id 商品id
     * @return 查询个数,为1则有,否则没有
     */
    @Select("Select COUNT(*) from lmonkey_user_collection where user_id =#{user_id} and product_id = #{product_id}")
    int queryCollection(String user_id,String product_id);

    /**
     * 查询用户的收藏商品个数
     * @param user_id 用户id
     * @return 收藏商品的个数
     */
    @Select("select COUNT(*) from lmonkey_user_collection where user_id = #{uid}")
    int queryUserCollectionCount(@Param("uid")String user_id);

    /**
     * 查询该用户下的所有收藏
     * @param uid 用户id
     * @return 收藏对象合集
     */
    @Select("select * from lmonkey_user_collection where user_id = #{uid}")
    List<Collection> queryUserCollection(@Param("uid")String uid);

}
