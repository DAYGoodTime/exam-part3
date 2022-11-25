package com.day.examp3.services;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

public interface CategoryServices {

    /**
     * 根据分类查询所有的关键词
     * @param category 分类
     * @return 关键词集合
     */
    List<String> queryKeyWordsByCategory(String category);

    /**
     * 查询所有父分类
     * @return 集合的方式存储父分类,Map的k代表父分类的id和name,v为对应的值
     */
    List<Map<String, String>> queryAllFatherCategory();

    /**
     * 查询所有有子类的父亲
     * @return 父亲id作为key,value为其下的子类name集合
     */
    List<JSONObject> queryAllFatherWithChildren();

    /**
     * 查询所有没有子类的父亲
     * @return 父亲外部名集合
     */
    List<String> queryAllFatherWithNoChildren();

}
