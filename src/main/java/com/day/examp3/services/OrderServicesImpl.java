package com.day.examp3.services;

import com.day.examp3.mapper.OrderMapper;
import com.day.examp3.mapper.PaymentMapper;
import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.Order;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class OrderServicesImpl  implements OrderServices{

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ProductMapper productMapper;

    @Autowired
    PaymentMapper paymentMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProductServicesImpl productServices;

    @Override
    public List<Order> getUserFullOrdersByProName(String product_name, String user_id) {
        List<Order> orgList = new ArrayList<>() ;
        List<String> proIds;
        proIds = productServices.getProIdsByName(product_name);
        for (String pid: proIds){
            List<Order> result;
            result = orderMapper.getUserOrdersByProId(pid,user_id);
            if(result!=null &&!result.isEmpty()){
                orgList.addAll(result);
            }
        }
        return orgList;
    }

    @Override
    public List<Order> getUserOrders(String status, String user_id) {
        if(status==null ||status.isEmpty()){
            return orderMapper.getFullOrdersByUserId(user_id);
        }else return orderMapper.getFullOrdersByStatus(status,user_id);

    }

}
