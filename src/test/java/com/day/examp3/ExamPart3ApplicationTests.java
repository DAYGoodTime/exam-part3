package com.day.examp3;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.day.examp3.mapper.OrderMapper;
import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.Order;
import com.day.examp3.pojo.Product;
import com.day.examp3.pojo.User;
import com.day.examp3.utils.NanoIdUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ExamPart3ApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;

    @Test
    void order(){
        List<Order> orgList = orderMapper.getFullOrdersByUserId("1");
        User user = userMapper.selectById("1");
        for (Order order:orgList
             ) {
            order.setUser(user);
        }
    }

    @Test
    void contextLoads(HttpServletRequest request){
        String path = ClassUtils.getDefaultClassLoader().getResource("static/userAvr").getPath();
        System.out.println(path);
    }

    @Test
    void user(){
        User user = userMapper.selectById(1);
        System.out.println(user.toString());
        user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
        userMapper.updateById(user);
    }

    @Test
    void addproduct(){
        for (int i = 9; i <=16; i++) {
            Product product = new Product();
            product.setName("【最家】时尚家居摆件简约创意装饰品");
            product.setPicture("/static/img/i"+i+".jpg");
            product.setKeywords("life");
            product.setFixedPrice(288.00);
            productMapper.insert(product);
        }
    }

    @Test
    void print(){
        List<Map<String, Object>> shoppingList = userMapper.getUserShoppingList("1");
        int i = 1;
        for (Map<String, Object> map:shoppingList) {
            for (Map.Entry<String,Object> entry: map.entrySet()
                 ) {
                System.out.println("["+i+"]key:"+entry.getKey()+" Value:"+entry.getValue());
            }
            i++;

        }
    }

}
