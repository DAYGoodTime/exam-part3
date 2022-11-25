package com.day.examp3.servicesImpl;

import com.day.examp3.mapper.OrderMapper;
import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.pojo.Order;
import com.day.examp3.pojo.Product;
import com.day.examp3.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductServicesImpl implements ProductServices {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CategoryServicesImpl categoryServices;

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

    @Override
    public List<Product> getProductByName(String name) {
        if(name==null ||name.isEmpty()) return productMapper.getAllProducts();
        else return productMapper.getProductByName(name);
    }

    @Override
    public Map<String,List<Product>> queryByCategory(String category) {
        List<String> keywords = categoryServices.queryKeyWordsByCategory(category);
        Map<String,List<Product>> products = new HashMap<>();
        for (String keyword:keywords) {
            products.put(keyword,productMapper.queryByKeyWord(keyword));
        }
        return products;
    }

    @Override
    public List<Product> queryByCategoryWithDesc(String category) {
        List<String> keywords = categoryServices.queryKeyWordsByCategory(category);
        return productMapper.queryProductByMultiKeyWordWithDesc(keywords);
    }

    @Override
    public List<Product> queryByCategoryWithAsce(String category) {
        List<String> keywords = categoryServices.queryKeyWordsByCategory(category);
        return productMapper.queryProductByMultiKeyWordWithAsce(keywords);
    }

    @Override
    public List<Product> queryUserLastFiveProduct(String user_id) {
        List<Order> orders = orderMapper.getLastFiveOrdersByUserId(user_id);
        List<Product> productList = new ArrayList<>(orders.size());
        for (Order order : orders) {
            if(!productList.contains(order.getProduct())){
                productList.add(order.getProduct());
            }
        }
        return productList;
    }

    @Override
    public boolean ProductStorageReduce(String pid, Integer amount) {
        Product product = productMapper.selectById(pid);
        product.setStorage(product.getStorage()-amount);
        return productMapper.updateById(product) == 1;
    }
}
