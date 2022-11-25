package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据名字去匹配相关的商品
     * @param name 需要匹配的商品名
     * @return 商品集合
     */
    @Select("select * from lmonkey_product where name like CONCAT('%',#{name},'%')")
    List<Product> getProductByName(@Param("name")String name);

    /**
     * 查询所有商品
     * @return 所有商品对象集合
     */
    @Select("select * from lmonkey_product")
    List<Product> getAllProducts();

    /**
     * 查询关键词下的所有产品
     * @param keyword 关键词
     * @return 匹配的产品
     */
    @Select("select * from lmonkey_product where keywords = #{keyword}")
    List<Product> queryByKeyWord(@Param("keyword")String keyword);

    /**
     * 根据关键词集合查询商品并以价格进行降序排序
     * @param keywords 关键词集合
     * @return 商品
     */
    List<Product> queryProductByMultiKeyWordWithDesc(List<String> keywords);

    /**
     * 根据关键词集合查询商品并以价格进行升序排序
     * @param keywords 关键词集合
     * @return 商品
     */
    List<Product> queryProductByMultiKeyWordWithAsce(List<String> keywords);
}
