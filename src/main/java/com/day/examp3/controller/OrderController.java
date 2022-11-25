package com.day.examp3.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.servicesImpl.OrderServicesImpl;
import com.day.examp3.servicesImpl.ProductServicesImpl;
import com.day.examp3.utils.DataUtil;
import com.day.examp3.utils.NanoIdUtils;
import com.day.examp3.utils.StatCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductServicesImpl productServices;

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

    @Autowired
    StatCode statCode;

    protected static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    @Autowired
    ShoppingMapper shoppingMapper;

    /**
     *获取用户最后五个购买过的订单,并转为商品推荐
     * 如果列表为空或者为匿名用户,则获取关键词为guess的商品
     */
    @RequestMapping("/guestYouLike")
    @ResponseBody
    public String guessYouLike(@RequestBody JSONObject json){
        String user_id = json.getString("uid");
        List<Product> productList;
        if(user_id==null||user_id.isEmpty()) {
            productList = productMapper.queryByKeyWord("guess");
            return statCode.PassCode("操作成功",productList);
        }else {
            productList = productServices.queryUserLastFiveProduct(user_id);
        }
        if(productList.isEmpty()){
            productList = productMapper.queryByKeyWord("guess");
        }
        return statCode.PassCode("操作成功",productList);
    }

    @RequestMapping(value = "/preCreatOrder",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String preCreateOrder(@RequestBody JSONObject json,Model model,HttpSession session){
        String user_id = (String) session.getAttribute("user_id");
        List<JSONObject> objList = json.getList("products",JSONObject.class);
        BigDecimal total = new BigDecimal(0);
        String BoundId = NanoIdUtils.randomNanoId(); //为同一批次的订单生成唯一的Id
        List<Order> orders = new ArrayList<>();
        for (JSONObject obj:objList) {
            Order preOrder = new Order();
            preOrder.setUserId(user_id);
            String pid = obj.getString("pid");

            preOrder.setBoundId(BoundId);//设置批次Id

            Product pro = productMapper.selectById(pid);
            preOrder.setProductId(pid); //设置对应商品
            preOrder.setAmount(obj.getInteger("productAmount")); //设置购买数量
            preOrder.setTotalPrice(pro.getFixedPrice()); //设置总额
            //查询用户默认的配送地址
            Address defaultAddress = addressMapper.queryDefaultUserAddress(user_id);
            if(defaultAddress==null){
                return statCode.ErrorCode("请先设置您的默认地址");
            }
            preOrder.setReceiveAddress(defaultAddress);
            preOrder.setReceiveAddressId(defaultAddress.getReceiveAddressId());
            //设置初始状态
            preOrder.setStatus("待支付");
            //设置默认的支付方式
            preOrder.setPaymentId(1);
            //设置订单时间
            preOrder.setOrderTime(new Timestamp(new Date().getTime()));
            //生成12位的订单id
            String order_id = NanoIdUtils.randomNanoId(new SecureRandom(),ALPHABET,12);
            preOrder.setOrderId(order_id);
            orders.add(preOrder);
            //统计金额
            total = total.add(preOrder.getTotalPrice());
            shoppingMapper.delCartByPidAndUid(user_id,pid);//删除在购物车的物品
        }
        if(!orderServices.addBatchOrders(orders)){
            //TODO 上传数据库失败
            return statCode.ErrorCode("创建订单失败");
        }
        model.addAttribute("totalprice",total);
        return statCode.PassCode("操作成功","/user/Order/"+BoundId);
    }

    @RequestMapping("/user/Order/{BoundId}")
    public String ConfirmOrder(Model model,HttpSession session,@PathVariable("BoundId")String BoundId){
        String user_id = (String) session.getAttribute("user_id");
        //查询同一批次的oderId
        List<Order> orders = orderMapper.getBoundOrderByUserId(user_id,BoundId);
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
        model.addAttribute("orders",orders);
        //批次ID也存到模型当中
        model.addAttribute("boundId",BoundId);
        return "order";
    }
    @RequestMapping("/user/myOrder/{status}")
    public String toMyOrder(Model model, HttpSession session,
                            @PathVariable("status")String status,
                            String search,Boolean isRedirect
    ){
        String user_id = (String) session.getAttribute("user_id");
        if(search!=null &&!search.isEmpty()) {
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
        model.addAttribute("status",status);
        if(isRedirect!=null && isRedirect) return "redirect:/user/myOrder";
        return "myorderq";
    }

    @RequestMapping("/user/confirmToReceiver/{oid}")
    public String confirmToReceiver(@PathVariable("oid")String order_id,Model model,HttpSession session){
        Order order = orderMapper.selectById(order_id);
        order.setStatus("已收货");
        orderMapper.updateById(order);
        return toMyOrder(model,session,null,null,true);
    }

    @RequestMapping(value = "/user/myOrder",method = RequestMethod.GET)
    public String toMyOrderSearch(Model model, HttpSession session,@RequestParam(value = "search",required = false)String search){
        return toMyOrder(model,session,null,search,false);
    }

    @RequestMapping("/updateOrder")
    @ResponseBody
    public String updateOrder(@RequestBody JSONObject object){
        String method = object.getString("method");
        if(method==null||method.isEmpty()){
            return statCode.ErrorCode("方法参数为空");
        }
        switch (method){
            case "setAddress":return setOrderAddress(object);
            case "setPayment":return setPayment(object);
            default:return statCode.ErrorCode("方法参数无效");
        }
    }
    public String setOrderAddress(JSONObject object){
        String BoundId = object.getString("boundId");
        String user_id = object.getString("uid");
        String addId =object.getString("addId");
        List<Order> orders = orderMapper.getBoundOrderByUserId(user_id,BoundId);
        if(orders==null||orders.isEmpty()){
            return statCode.ErrorCode("订单查询失败");
        }
        for (Order order:orders) {
            order.setReceiveAddressId(Long.parseLong(addId));
        }
        if(orderServices.updateBatchOrdersWithAdd(orders)){
          return statCode.PassCodeOnly("修改成功");
        } else statCode.ErrorCode("修改失败");
        return statCode.ErrorCode("未知错误");
    }

    public String setPayment(JSONObject object){
        String BoundId = object.getString("boundId");
        String user_id = object.getString("uid");
        Integer PaymentId = object.getInteger("paymentId");
        List<Order> orders = orderMapper.getBoundOrderByUserId(user_id,BoundId);
        if(orders==null||orders.isEmpty()){
            return statCode.ErrorCode("订单查询失败");
        }
        for (Order order:orders) {
            order.setPaymentId(PaymentId);
        }
        if(orderServices.updateBatchOrdersWithPayment(orders)){
            return statCode.PassCodeOnly("修改成功");
        } else statCode.ErrorCode("修改失败");
        return statCode.ErrorCode("未知错误");
    }



    //支付请求接口
    @RequestMapping("/user/OrderPayReq")
    @ResponseBody
    public String payment(@RequestBody JSONObject object){
        String BoundId = object.getString("boundId");
        String user_id = object.getString("uid");
        if(BoundId==null||BoundId.isEmpty()||user_id==null||user_id.isEmpty()){
            return statCode.ErrorCode("参数错误");
        }
        List<Order> orders = orderMapper.getBoundOrderByUserId(user_id,BoundId);
        if(orders==null||orders.isEmpty()){
            return statCode.ErrorCode("获取订单失败");
        }
        Integer paymentId = orders.get(0).getPaymentId();
        if(paymentId!=1 && paymentId!=3){
            return statCode.ErrorCode("支付方式有误");
        }
        JSONObject order = new JSONObject();
        order.put("orders",orders);
        order.put("boundId",BoundId);
        switch (paymentId){
            case 1:return wechatPay(order);
            case 3:return AliyPay(order);
            default:return statCode.ErrorCode("支付方式有误");
        }
    }


    private static final String notifyUrl = "";

    private static final String appid="WechatPayThirPartyappid";
    private static final String appsec="ThirPartyappsec";

    public String wechatPay(JSONObject object){
        System.out.println("[LOG]:尝试调用微信支付接口");
        List<Order> orders = object.getList("orders",Order.class);
        BigDecimal amount = new BigDecimal(0);
        String orderno = object.getString("boundId") + DataUtil.getCurrentTimeAsString();
        for (Order order:orders) {
            amount=amount.add(order.getTotalPrice());
        }
        String payfor = "订单号:"+object.getString("boundId")+"需要支付的金额:"+amount;
        JSONObject json = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        StringBuilder sign = new StringBuilder();
        //amount=0.10&appid=qm_fvmZ5vHun7&orderno=120912893872373544&payfor=测试产品&timestamp=20220305214232&appsecret=XXXXXXXX
        //TODO DEBUGMODE:订单价格默认为0.01
        String DEBUGAMOUNT = "0.01";
        sign.append("amount=").append(DEBUGAMOUNT).append("&")
                .append("appid=").append(appid).append("&")
                .append("orderno=").append(orderno).append("&")
                .append("payfor=").append(payfor).append("&")
                .append("timestamp=").append(time).append("&")
                .append("appsecret=").append(appsec);
        String md5sign = SecureUtil.md5(String.valueOf(sign)); //使用了Hutool工具类进行Md5加密
        //HTTP请求
        json.put("amount",DEBUGAMOUNT);
        json.put("appid",appid);
        json.put("orderno",orderno);
        json.put("payfor",payfor);
        json.put("timestamp",time);
        json.put("sign",md5sign);
        String url = "http://pay.shazure.com/payservice/createorder";
        RestTemplate restTemplate = new RestTemplate();
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> entity = new HttpEntity<>(json,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,entity,String.class);
        JSONObject response = JSONObject.parse(responseEntity.getBody()); //{"msg":"调用成功! ", " code" : 1}
        //TODO 放回用AJAX接收
        if(response==null||response.isEmpty()){
            return statCode.ErrorCode("支付失败,对接平台也许失联了");
        }
        if(response.getString("success").equals("false")){
            System.out.println(response.getString("message"));
            System.out.println("支付请求:"+sign);
            return statCode.ErrorCode("支付失败,请查看控制台");
        }else {
            if(response.getString("success").equals("true")){
                for (Order order:orders) {
                    order.setStatus("待收货");
                    productServices.ProductStorageReduce(order.getProductId(),order.getAmount());
                }
                orderServices.updateBatchOrdersWithStatus(orders);
                JSONObject Retrun = new JSONObject();
                Retrun.put("code","200");
                Retrun.put("method","wechat");//partnerno expirein
                Retrun.put("partnerno",response.getJSONObject("data").getString("partnerno"));
                Retrun.put("expirein",response.getJSONObject("data").getString("expirein"));
                return Retrun.toJSONString();
            }else {
                System.out.println(response);
                return statCode.ErrorCode("请求返回了意外的信息");
            }
        }

    }

    private static String privateKey = "privateKey";
    private static String aliyPublicKey = "aliyPublicKey";
    private static String ailypAppid = "ailypAppid";


    public String AliyPay(JSONObject object){
        System.out.println("[LOG]正在调用支付宝接口");
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do",ailypAppid,privateKey,"json","UTF-8",aliyPublicKey,"RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl("http://localhost:8080/order/ok");
        JSONObject bizContent = new JSONObject();

        List<Order> orders = object.getList("orders",Order.class);
        BigDecimal amount = new BigDecimal(0);
        for (Order order:orders) {
            amount=amount.add(order.getTotalPrice().multiply(new BigDecimal(order.getAmount())));
            productServices.ProductStorageReduce(order.getProduct().getProductId(),order.getAmount());
        }
        String payfor = "订单号"+object.getString("boundId");
        bizContent.put("out_trade_no",NanoIdUtils.randomNanoId());//object.getString("boundId")
        bizContent.put("total_amount", amount);
        bizContent.put("subject", payfor);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
//bizContent.put("time_expire", "2022-08-01 22:00:00");

//// 商品明细信息，按需传入
//JSONArray goodsDetail = new JSONArray();
//JSONObject goods1 = new JSONObject();
//goods1.put("goods_id", "goodsNo1");
//goods1.put("goods_name", "子商品1");
//goods1.put("quantity", 1);
//goods1.put("price", 0.01);
//goodsDetail.add(goods1);
//bizContent.put("goods_detail", goodsDetail);

//// 扩展信息，按需传入
//JSONObject extendParams = new JSONObject();
//extendParams.put("sys_service_provider_id", "2088511833207846");
//bizContent.put("extend_params", extendParams);
        request.setBizContent(bizContent.toString());
        String response;
        try{
            response = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            return statCode.ErrorCode("出现错误:"+e.getErrMsg());
        }

        JSONObject code = new JSONObject();
        code.put("code","200");
        code.put("msg","调用支付成功");
        code.put("method","alipay");
        code.put("data",response);
        for (Order order:orders) {
            order.setStatus("待收货");
        }
        orderServices.updateBatchOrdersWithStatus(orders);
        return code.toJSONString();
    }
}
