package com.day.examp3.mapper;

import com.day.examp3.pojo.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用于查询地级市联动的Mapper
 */
@Mapper
public interface LocationMapper {

    @Select("select * from china where Pid = #{pid}")
    List<Location> getLocations(@Param("pid")Integer pid);

    @Select("select * from china where id = #{id}")
    Location getLocationsById(@Param("id")Integer id);
}
