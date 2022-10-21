package com.day.examp3.services;


import java.util.List;

public interface ProductServices {

    /**
     * 根据名字匹配所有有关的产品id
     * @param name 关键词
     * @return 匹配的产品ID集合
     */
    List<String> getProIdsByName(String name);

}
