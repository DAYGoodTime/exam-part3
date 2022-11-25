package com.day.examp3.servicesImpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.day.examp3.mapper.OrderMapper;
import com.day.examp3.mapper.PaymentMapper;
import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.Order;
import com.day.examp3.services.OrderServices;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderServicesImpl  implements OrderServices {

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
    public List<Order> getUserOrders(String status,String user_id) {
        if(status==null ||status.isEmpty()){
            return orderMapper.getFullOrdersByUserId(user_id);
        }else
            return orderMapper.getFullOrdersByStatusWithUid(user_id,status);
    }

    @Override
    public Page<Order> getOrdersPageByStatus(Page<Order> page, String status) {
        page.setTotal(orderMapper.getOrdersCountByStatus(status));
        page.setPages(page.getTotal()/ page.getSize());
        if(page.getCurrent()>page.getPages()) page.setCurrent(page.getPages());
        if (page.getCurrent()==0){
            page.setCurrent(1L);
        }
        page.setRecords(orderMapper.getFullOrdersPageByStatus(status,(page.getCurrent()-1)*page.getSize(),page.getSize()));
        return page;
    }

    @Override
    public boolean addBatchOrders(List<Order> orders) {
        return orderMapper.addAllOrders(orders)==orders.size();
    }

    @Override
    public boolean updateBatchOrdersWithAdd(List<Order> orders) {
        return orderMapper.updateAllOrdersWithAddId(orders)==orders.size();
    }
    @Override
    public boolean updateBatchOrdersWithPayment(List<Order> orders) {
        return orderMapper.updateAllOrdersWithPayId(orders)==orders.size();
    }
    @Override
    public boolean updateBatchOrdersWithStatus(List<Order> orders) {
        return orderMapper.updateAllOrdersWithStatus(orders)==orders.size();
    }

    @Override
    public Page<Order> queryAllOrdersPage(Page<Order> page) {
        page.setTotal(orderMapper.queryAllOrderCounts());
        page.setPages(page.getTotal()/ page.getSize());
        if(page.getCurrent()>page.getPages()) page.setCurrent(page.getPages());
        if (page.getCurrent()==0){
            page.setCurrent(1L);
        }
        page.setRecords(orderMapper.queryAllOrdersByPage((page.getCurrent()-1)*page.getSize(),page.getSize()));
        return page;
    }
    @Override
    public Page<Order> queryOrdersPageByBoundId(Page<Order> page,String BoundId) {
        page.setTotal(orderMapper.queryOrdersByBoundId(BoundId).size());
        page.setPages(page.getTotal()/ page.getSize());
        if(page.getCurrent()>page.getPages()) page.setCurrent(page.getPages());
        page.setRecords(orderMapper.queryOrdersByPageAndBoundId((page.getCurrent()-1)*page.getSize(),page.getSize(),BoundId));
        return page;
    }

    @Override
    public Map<String, Integer> queryOrderCountsByStatus(String uid) {
        Map<String, Integer> map = new HashMap<>();
        map.put("已收货",orderMapper.getUserOrderCountsByStatus(uid,"已收货"));
        map.put("待收货",orderMapper.getUserOrderCountsByStatus(uid,"待收货"));
        map.put("待支付",orderMapper.getUserOrderCountsByStatus(uid,"待支付"));
        return map;
    }

}
