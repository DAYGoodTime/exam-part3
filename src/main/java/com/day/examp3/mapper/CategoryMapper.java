package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 分类相关的Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据父分类查询所有的子分类
     * @param father 父分类内部名
     * @return 分类以key:value的map方式的集合
     */
    @Select("select children_name,children_category from lmonkey_category where parent_category = #{father}")
    List<Map<String,String>> queryChildrenByFather(@Param("father")String father);
    /**
     * 查询所有有子分类的父分类
     * @return kv集合的方式返回,包括父的内部名,和外部名
     */
    @Select("select distinct parent_category,parent_name from lmonkey_category where children_category is not null and children_name is not null")
    List<Map<String,String>> queryAllFatherCategoryWithChildren();

    /**
     * 查询所有没有子分类的父分类
     * @return kv集合的方式返回,包括父的内部名,和外部名
     */
    @Select("select distinct parent_category,parent_name from lmonkey_category where children_category is null and children_name is null")
    List<Map<String,String>> queryAllFatherCategoryWithNoChildren();
}
