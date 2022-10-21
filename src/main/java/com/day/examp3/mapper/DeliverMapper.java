package com.day.examp3.mapper;

import com.day.examp3.pojo.Deliver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeliverMapper {

    @Select("select * from lmonkey_deliver")
    List<Deliver> queryAllDeliver();

}
