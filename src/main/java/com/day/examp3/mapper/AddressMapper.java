package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    /**
     * 根据用户ID查询所有的地址
     * @param user_id 用户id
     * @return List的地址类
     */
    @Select("select * from lmonkey_receive_address where user_id = #{uid} and is_deleted = 0")
    List<Address> queryAddressListByUserId(@Param("uid")String user_id);

    /**
     * 查询用户的默认地址
     * @param user_id 用户id
     * @return Address类
     */
    @Select("select * from lmonkey_receive_address where user_id = #{uid} and is_default = 1 and is_deleted = 0")
    Address queryDefaultUserAddress(@Param("uid")String user_id);

    /**
     * 查询用户下的地址
     * @param user_id 用户ID
     * @param addId 地址ID
     * @return Address类
     */
    @Select("select * from lmonkey_receive_address where user_id = #{uid} and receive_address_id = #{addId} and is_deleted = 0")
    Address queryUserAddressById(@Param("uid")String user_id,@Param("addId")Integer addId);

    /**
     * 计算用户下非逻辑删除的地址数量
     * @param user_id 用户ID
     * @return 查询到非逻辑删除的地址个数
     */
    @Select("select COUNT(*) from lmonkey_receive_address where user_id = #{uid} and is_deleted = 0")
    int queryAddressCountByUid(@Param("uid")String user_id);

    /**
     * 使用逻辑删除(通过用户id和地址id)
     * @param uid 用户id
     * @param addId 地址
     * @return 操作个数
     */
    @Update("update lmonkey_receive_address set is_deleted = 1 where receive_address_id = #{addId} and user_id = #{uid}")
    int delAddByUserIdByLogic(@Param("uid")String uid,@Param("addId")Integer addId);

    /**
     * 使用逻辑删除(通过地址id)
     * @param addId 地址
     * @return 操作个数
     */
    @Update("update lmonkey_receive_address set is_deleted = 1 where receive_address_id = #{addId}")
    int delAddByAddIdByLogic(@Param("addId")Integer addId);
}

