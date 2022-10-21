package com.day.examp3.controller;

import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.services.OrderServicesImpl;
import com.day.examp3.services.UserServicesImpl;
import com.day.examp3.utils.NanoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserServicesImpl userServices;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderServicesImpl orderServices;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    PaymentMapper paymentMapper;

    @Autowired
    DeliverMapper deliverMapper;

    protected static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    @RequestMapping(value = "/preCreatOrder",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String preCreateOrder(@RequestBody JSONObject json,Model model,HttpSession session){
        Order preOrder = new Order();
        String user_id = (String) session.getAttribute("user_id");
        preOrder.setUserId(user_id);
        List<JSONObject> objList = json.getList("products",JSONObject.class);
        List<Product> proList = new ArrayList<>();
        double total = 0;
        for (JSONObject obj:objList) {
            Product pro = productMapper.selectById(obj.getString("pid"));
            int amount = obj.getInteger("productAmount");
            total = total+pro.getFixedPrice()*amount;
            pro.setO_amount(amount);
            proList.add(pro);
        }
        preOrder.setTotalPrice((long) total); //设置总额
        //查询用户默认的配送地址
        Address defaultAddress = addressMapper.queryDefaultUserAddress(user_id);
        preOrder.setReceiveAddress(defaultAddress);
        preOrder.setReceiveAddressId(defaultAddress.getReceiveAddressId());
        //设置初始状态
        preOrder.setStatus("待收货");
        //设置订单时间
        preOrder.setOrderTime(new Timestamp(new Date().getTime()));
        //生成12位的订单id
        String order_id = NanoIdUtils.randomNanoId(new SecureRandom(),ALPHABET,12);
        preOrder.setOrderId(order_id);
        preOrder.setProductList(proList);
        //TODO 上传到数据库
        return "/user/Order/"+order_id;
    }

    @RequestMapping("/user/Order/{orderId}")
    public String ConfirmOrder(Model model,HttpSession session,@PathVariable("orderId") String orderId){
        String user_id = (String) session.getAttribute("user_id");
        Order order = orderMapper.getUserOrderById(user_id,orderId);
        //传入用户信息
        User user = userMapper.selectById(user_id);
        order.setUser(user);
        // 传入地址信息
        List<Address> addressList = addressMapper.queryAddressListByUserId(user_id);
        model.addAttribute("addressList",addressList);
        // 传入支付信息
        List<Payment> paymentList = paymentMapper.queryAllPayment();
        model.addAttribute("paymentList",paymentList);
        // 传入快递信息
        List<Deliver> delivers = deliverMapper.queryAllDeliver();
        model.addAttribute("delivers",delivers);
        //传入oder到模型中
        model.addAttribute("order",order);
        return "order";
    }


    @RequestMapping("/user/myOrder/{status}")
    public String toMyOrder(Model model, HttpSession session,
                            @PathVariable("status")String status,
                            String search
    ){

        String user_id = (String) session.getAttribute("user_id");
        User user = userMapper.selectById(user_id);
        if(search!=null) {
            String order = "^.{12}$"; //订单号匹配
            List<Order> orderList = new ArrayList<>();
            if(search.matches(order)){
                orderList.add(orderMapper.getUserOrderById(user_id,search));
            }else {
                orderList.addAll(orderServices.getUserFullOrdersByProName(search,user_id));
            }

            model.addAttribute("orders",orderList);
            return "myorderq";
        }
        List<Order> orderList = orderServices.getUserOrders(status,user_id);
        if(orderList==null){
            //TODO 查询失败操作
        }
        //对订单传入用户对象
        model.addAttribute("orders",orderList);
        return "myorderq";
    }
    @RequestMapping(value = "/user/myOrder",method = RequestMethod.GET)
    public String toMyOrderSearch(Model model, HttpSession session,@RequestParam(value = "search",required = false)String search){
        return toMyOrder(model,session,null,search);
    }
}
