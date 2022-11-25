package com.day.examp3.controller;

import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.Address;
import com.day.examp3.pojo.Order;
import com.day.examp3.pojo.Product;
import com.day.examp3.utils.AESUtil;
import com.day.examp3.utils.StatCode;
import com.day.examp3.servicesImpl.CategoryServicesImpl;
import com.day.examp3.servicesImpl.OrderServicesImpl;
import com.day.examp3.servicesImpl.ProductServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
/**
 * 用于初始化各个页面需要初始化参数的页面
 */
public class InitializationController {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductServicesImpl productServices;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    CategoryServicesImpl categoryServices;
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    StatCode statCode;
    @Autowired
    AESUtil aesUtil;

    /**
     * 首页初始化
     *
     * @param model
     * @return
     */
    @RequestMapping("/main")
    public String main(Model model, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "FLOWERS");
        List<Product> products_flower = productMapper.selectByMap(map);
        model.addAttribute("products_flower", products_flower);
        map.replace("keywords", "NORDIC_WALL");
        List<Product> products_nordic = productMapper.selectByMap(map);
        model.addAttribute("products_nordic", products_nordic);
        map.replace("keywords", "UNIQUE_TRUE_LOVE");
        List<Product> products_love = productMapper.selectByMap(map);
        model.addAttribute("products_love", products_love);
        return "index";
    }

    @RequestMapping("/")
    public String toIndex(Model model, HttpSession session){
        return main(model,session);
    }

    @RequestMapping("/login")
    public String toLogin(
            Model model, Boolean isRedirect,
            @ModelAttribute("msg")String msg
    ){
        if(msg != null && !msg.isEmpty()){
            model.addAttribute("msg",msg);
            return "login";
        }else {
            model.addAttribute("msg","公共场所不建议自动登录，以防账号丢失");
            return "login";
        }
    }

    @RequestMapping("/register")
    public String toRegister(){
        return "reg";
    }


    @RequestMapping("/user/changePassword")
    public String toChangePwd(){
        return "remima";
    }


    @RequestMapping(value = "/order/ok")
    public String toOk(){
        return "ok";
    }

    @RequestMapping("/forget")
    public String toForget(){
        return "forget";
    }

    @RequestMapping("/resetPwd/{hexuid}")
    public String toRestPwd(Model model, @PathVariable("hexuid")String hexuid){
        String uid = aesUtil.decryptStrHex(hexuid);
        model.addAttribute("uid",uid);
        return "restPwd";
    }

    @RequestMapping(value = "/main/search")
    public String searchProduct(@RequestParam(value = "search", required = false) String name, Model model) {
        List<Product> productList = productServices.getProductByName(name);
        model.addAttribute("productList", productList);
        return "search";
    }

    @RequestMapping("/user/address")
    public String toAddress(Model model, HttpSession session) {
        String user_id = (String) session.getAttribute("user_id");

        List<Address> addressList = addressMapper.queryAddressListByUserId(user_id);
        if (addressList == null) {
            //TODO 查询失败操作
        }
        model.addAttribute("addressList", addressList);
        return "address";
    }

    /**
     * Paint页面初始化
     *
     * @param model
     * @return
     */
    @RequestMapping("/paint")
    public String toPaint(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "SIMPLE_MODERN");
        List<Product> products_modern = productMapper.selectByMap(map);
        model.addAttribute("products_modern", products_modern);
        map.replace("keywords", "EUROPEAN");
        List<Product> products_european = productMapper.selectByMap(map);
        model.addAttribute("products_european", products_european);
        map.replace("keywords", "AMERICAN");
        List<Product> products_american = productMapper.selectByMap(map);
        model.addAttribute("products_american", products_american);
        map.replace("keywords", "AMERICAN_2");
        List<Product> products_american2 = productMapper.selectByMap(map);
        model.addAttribute("products_american2", products_american2);
        return "paint";
    }

    @RequestMapping("/flowerDer")
    public String toFlowerDer(Model model) {
        Map<String, List<Product>> flowerDer = productServices.queryByCategory("flowerDer");
        model.addAttribute("proList", flowerDer.get("proList"));
        model.addAttribute("vases", flowerDer.get("vase"));
        return "flowerDer";
    }

    @RequestMapping("/flowerDer/proList")
    public String toProList(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "proList");
        List<Product> proList = productMapper.selectByMap(map);
        model.addAttribute("proList", proList);
        return "proList";
    }

    @RequestMapping("/flowerDer/vaseProList")
    public String toVase_proList(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "vase");
        List<Product> vases = productMapper.selectByMap(map);
        model.addAttribute("vases", vases);
        return "vase_proList";
    }

    @RequestMapping(value = {"/decoration", "/decoration/{order}"})
    public String toDecoration(Model model, @PathVariable(value = "order", required = false) String order) {
        model.addAttribute("currentPage", "decoration");
        if (order != null && !order.isEmpty()) {
            if (order.equals("desc")) {
                List<Product> bzproList = productServices.queryByCategoryWithDesc("decoration");
                model.addAttribute("bzproList", bzproList);
                return "decoration";
            } else if (order.equals("asce")) {
                List<Product> bzproList = productServices.queryByCategoryWithAsce("decoration");
                model.addAttribute("bzproList", bzproList);
                return "decoration";
            }
        } else {
            Map<String, List<Product>> decorations = productServices.queryByCategory("decoration");
            model.addAttribute("bzproList", decorations.get("bzproList"));
            model.addAttribute("zbproList", decorations.get("zbproList"));
            return "decoration";
        }
        return null;
    }

    @RequestMapping("/decoration/zbproList")
    public String toZbproList(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "zbproList");
        List<Product> zbproList = productMapper.selectByMap(map);
        model.addAttribute("zbproList", zbproList);
        return "zbproList";
    }

    @RequestMapping("/decoration/bzproList")
    public String toBzroList(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "bzproList");
        List<Product> bzproList = productMapper.selectByMap(map);
        model.addAttribute("bzproList", bzproList);
        return "bzproList";
    }

    @RequestMapping("/perfume")
    public String toPerfume(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "AROMATHERAPY");
        List<Product> aromatherapys = productMapper.selectByMap(map);
        model.addAttribute("aromatherapys", aromatherapys);
        map.replace("keywords", "BURNER");
        List<Product> burners = productMapper.selectByMap(map);
        model.addAttribute("burners", burners);
        return "perfume";
    }

    @RequestMapping("/computer/cpu")
    public String cpu(Model model){
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "cpu");
        List<Product> productList = productMapper.selectByMap(map);
        model.addAttribute("cpus",productList);
        return "cpu";
    }
    @RequestMapping("/computer/card")
    public String card(Model model){
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "card");
        List<Product> productList = productMapper.selectByMap(map);
        model.addAttribute("cards",productList);
        return "card";
    }

    @RequestMapping("/idea")
    public String toIdea(Model model) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "fashion");
        List<Product> fashions = productMapper.selectByMap(map);
        model.addAttribute("fashions", fashions);
        map.replace("keywords", "novel");
        List<Product> novels = productMapper.selectByMap(map);
        model.addAttribute("novels", novels);
        map.replace("keywords", "life");
        List<Product> lifes = productMapper.selectByMap(map);
        model.addAttribute("lifes", lifes);
        return "idea";
    }

    @RequestMapping("/user/cartList")
    public String toCart(HttpSession session, Model model) {
        String user_id = (String) session.getAttribute("user_id");
        //刷新一下购物车数量
        int userShoppingCount = userMapper.getUserShoppingCount(user_id);
        session.setAttribute("userShoppings", userShoppingCount);

        List<Map<String, Object>> userShoppingList = userMapper.getUserShoppingList(user_id);
        model.addAttribute("shoppingListMap", userShoppingList);
        return "cart";
    }

    @Autowired
    PaymentMapper paymentMapper;

    @RequestMapping("/user/OrderDetail/{oid}")
    public String oderDetail(Model model, @PathVariable("oid") String oid, HttpSession session) {
        String user_id = (String) session.getAttribute("user_id");
        Order order = orderMapper.getUserOrderById(user_id, oid);
        order.setReceiveAddress(addressMapper.queryUserAddressById(user_id, Math.toIntExact(order.getReceiveAddressId())));
        order.setPayment(paymentMapper.getPayment(order.getPaymentId()));
        model.addAttribute("order", order);
        return "orderxq";
    }



}
