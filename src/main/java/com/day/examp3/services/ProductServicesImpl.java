package com.day.examp3.services;

import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductServicesImpl implements ProductServices {

    @Autowired
    ProductMapper productMapper;

    @Override
    public List<String> getProIdsByName(String name) {
        List<Product> productList;
        List<String> proIds = new ArrayList<>();
        productList = productMapper.getProductByName(name);
        if(productList==null) //TODO 查询失败操作
            return null;
        for (Product pro: productList) {
            proIds.add(pro.getProductId());
        }
        return proIds;
    }
}
