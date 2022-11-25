package com.day.examp3.services;


import com.day.examp3.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductServices {

    /**
     * 根据名字匹配所有有关的产品id
     * @param name 关键词
     * @return 匹配的产品ID集合
     */
    List<String> getProIdsByName(String name);

    /**
     * 根据商品名字查询商品,为空则全部查询
     * @param name 商品名字
     * @return 匹配的所有商品
     */
    List<Product> getProductByName(String name);

    /**
     * 根据分类进行所有产品的查询
     * @param category 分类
     * @return 匹配的所有商品,以Map的方式去对应List集合
     */
    Map<String,List<Product>> queryByCategory(String category);

    /**
     * 根据分类进行所有产品的查询,并以价格进行降序排序
     * @param category 分类
     * @return 匹配的所有商品,按价格进行降序排序
     */
    List<Product> queryByCategoryWithDesc(String category);
    /**
     * 根据分类进行所有产品的查询,并以价格进行升序排序
     * @param category 分类
     * @return 匹配的所有商品,按价格进行升序排序
     */
    List<Product> queryByCategoryWithAsce(String category);

    /**
     * 查询用户最后5个订单的商品
     * @param user_id 用户id
     * @return 匹配的商品对象
     */
    List<Product> queryUserLastFiveProduct(String user_id);

    /**
     * 减少该产品的库存
     * @param pid 产品Id
     * @param amount 数量
     * @return 是否成功
     */
    boolean ProductStorageReduce(String pid, Integer amount);
}
