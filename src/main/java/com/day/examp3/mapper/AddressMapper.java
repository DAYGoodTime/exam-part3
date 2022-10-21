package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    /**
     * 根据用户ID查询所有的地址
     * @param user_id 用户id
     * @return List的地址类
     */
    @Select("select * from lmonkey_receive_address where user_id = #{uid}")
    List<Address> queryAddressListByUserId(@Param("uid")String user_id);

    @Select("select * from lmonkey_receive_address where user_id = #{uid} and is_default = 1")
    Address queryDefaultUserAddress(@Param("uid")String user_id);

}

