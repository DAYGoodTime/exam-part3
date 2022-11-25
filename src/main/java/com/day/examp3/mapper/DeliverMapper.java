package com.day.examp3.mapper;

import com.day.examp3.pojo.Deliver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeliverMapper {
    /**
     * 一个简单查询所有存在的配送方式
     * @return
     */
    @Select("select * from lmonkey_deliver")
    List<Deliver> queryAllDeliver();

}
