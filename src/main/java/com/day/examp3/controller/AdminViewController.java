package com.day.examp3.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.day.examp3.config.OwnConfig;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.servicesImpl.OrderServicesImpl;
import com.day.examp3.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

/**
 * 大部分用于视图层跳转的控制器
 */
@Controller
public class AdminViewController<T> {
    public static final int MAX_FILE_SIZE = 5242880;//TODO 文件上传大小匹配config
    public static final int PageSize = 10;
    public static final int PageLength = 5;

    public static int Findex;
    public static int Lindex;
    public static int TotalPages;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderServicesImpl orderServices;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    OwnConfig ownConfig;
    @Autowired
    StatCode statCode;


    @RequestMapping("/admin")
    public String admin(Model model, HttpServletRequest request, HttpServletResponse response){
        Properties sysProperty=System.getProperties(); //系统属性
        String host;
        try {//获取主机ip
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("host",host);//获取主机地址
        model.addAttribute("sysName",sysProperty.getProperty("os.name"));//获取系统名称
        model.addAttribute("arch",sysProperty.getProperty("os.arch"));//获取系统架构
        model.addAttribute("sysVer",sysProperty.getProperty("os.version")); //获取系统版本
        model.addAttribute("javaVersion",sysProperty.getProperty("java.version"));//获取java版本
        model.addAttribute("data", DataUtil.DataToString(new Date(),DataUtil.Fsdf_cn));//获取时间
        model.addAttribute("FileUploadSize",ownConfig.getFileUpLoadSize());//获取文件上传大小

        return "/admin/index";
    }

    @RequestMapping("/admin/logout")
    public String adminLogout(HttpServletRequest request){
        request.getSession().invalidate();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies
        ) {
            cookie.setMaxAge(0);
        }
        return "redirect:/admin/login";
    }


    @RequestMapping("/admin/login")
    public String toAdminLogin(){
        return "admin/loginPage";
    }
    @RequestMapping("/adminPwdChange")
    public String toAdminPwdChange(){
        return "admin/changePwd";
    }
    @RequestMapping("/admin/sponsor")
    public String sponsor(){return "admin/sponserP";}
    @RequestMapping("/admin/webSetting")
    public String webSetting(Model model) throws IllegalAccessException {
        Class<OwnConfig> clz = OwnConfig.class;
        Map<String,Object> settings = new HashMap<>();
        for (Field field:clz.getDeclaredFields()) {//获取配置内容
            field.setAccessible(true);
            settings.put(field.getName(),field.get(ownConfig));
        }
        Set<String> keys = settings.keySet();
        model.addAttribute("keys",keys);
        model.addAttribute("settings",settings);
        return "admin/system";
    }

    //查询面板
    @RequestMapping(value = {"/admin/userPanel",
            "/admin/userPanel/{curPage}",
            "/admin/userPanel/{curPage}/{search}",
            "/admin/userPanel/{curPage}/opt/{option}",
            "/admin/userPanel/{curPage}/{search}/{option}"})
    public String userPanel(Model model,@PathVariable(value = "curPage",required = false)Integer curPage,
                            Boolean isRedirect,
                            @PathVariable(value = "search",required = false)String search,
                            @PathVariable(value = "option",required = false)String option){
        if(curPage==null ||curPage<1) curPage = 1;
        model.addAttribute("currentPage","user");
        QueryWrapper<User> wrapper = null;
        if(option!=null&&!option.isEmpty()){
            wrapper = new QueryWrapper<>();
            if(option.equals("admin")){
                wrapper.eq("is_admin","1");
            } else if (option.equals("normal")) {
                wrapper.eq("is_admin","0");
            }else {
                wrapper = null;
            }
            model.addAttribute("option",option);
        }//分类判断
        //查询所有用户
        Page<User> page = new Page<>(curPage,PageSize);
        if(search!=null&&!search.isEmpty()){//搜索判断
            if(wrapper==null){
                wrapper = new QueryWrapper<>();
            }
            wrapper.like("nick_name",search);
        }
        Page<User> userList = userMapper.selectPage(page,wrapper);
        pageHelper(model, (Page<T>) userList);
        model.addAttribute("userList",userList.getRecords());
        if(isRedirect==null||!isRedirect)return "/admin/design";
        else return "redirect:/admin/userPanel";
    }
    @RequestMapping(value = {"/admin/orderPanel/{curPage}","/admin/orderPanel","/admin/orderPanel/{curPage}/{search}","/admin/orderPanel/{curPage}/{option}"})
    public String orderPanel(Model model,@PathVariable(value = "curPage",required = false)Integer curPage,
                             Boolean isRedirect,
                             @PathVariable(value = "search",required = false)String search,
                             @PathVariable(value = "option",required = false)String status){
        if(curPage==null ||curPage<1) curPage = 1;
        model.addAttribute("currentPage","order");
        //查询所有订单
        Page<Order> page = new Page<>(curPage,PageSize);
        Page<Order> orderList = new Page<>(curPage,PageSize);
        if(status!=null&&!status.isEmpty()&&!status.equals("all")) {
            orderList = orderServices.getOrdersPageByStatus(page,status);
            model.addAttribute("option",status);
        }
        if(search!=null &&!search.isEmpty()){
            orderList = orderServices.queryOrdersPageByBoundId(page,search);
        }else if(status==null||status.isEmpty()||status.equals("all")) {
            orderList = orderServices.queryAllOrdersPage(page);
        }
        pageHelper(model, (Page<T>) orderList);
        model.addAttribute("orderList",orderList.getRecords());
        if(isRedirect==null||!isRedirect)return "/admin/design";
        else return "redirect:/admin/orderPanel";
    }
    @RequestMapping(value = {"/admin/productPanel","/admin/productPanel/{curPage}","/admin/productPanel/{curPage}/{search}"})
    public String productPanel(Model model,@PathVariable(value = "curPage",required = false)Integer curPage,Boolean isRedirect,@PathVariable(value = "search",required = false)String search){
        if(curPage==null ||curPage<1) curPage = 1;
        model.addAttribute("currentPage","product");
        //查询所有产品
        Page<Product> page = new Page<>(curPage,PageSize);
        QueryWrapper<Product> wrapper = null;
        if(search!=null&&!search.isEmpty()){
            wrapper = new QueryWrapper<>();
            wrapper.like("name",search);
        }
        Page<Product> productList = productMapper.selectPage(page,wrapper);
        pageHelper(model, (Page<T>) productList);
        model.addAttribute("productList",productList.getRecords());
        if(isRedirect==null||!isRedirect)return "/admin/design";
        else return "redirect:/admin/productPanel";
    }
    @RequestMapping(value = {"/admin/addressPanel","/admin/addressPanel/{curPage}","/admin/addressPanel/{curPage}/{search}"})
    public String addressPanel(Model model,@PathVariable(value = "curPage",required = false)Integer curPage,Boolean isRedirect,@PathVariable(value = "search",required = false)String search){
        if(curPage==null ||curPage<1) curPage = 1;
        model.addAttribute("currentPage","address");
        //查询所有地址
        Page<Address> page = new Page<>(curPage,PageSize);
        QueryWrapper<Address> wrapper = null;
        if(search!=null&&!search.isEmpty()){
            wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",search);
        }
        Page<Address> addressList = addressMapper.selectPage(page,wrapper);
        pageHelper(model, (Page<T>) addressList);
        model.addAttribute("addressList",addressList.getRecords());
        if (isRedirect==null||!isRedirect)return "/admin/design";
        else return "redirect:/admin/addressPanel";
    }
    @RequestMapping(value = {"/admin/commentPanel","/admin/commentPanel/{curPage}","/admin/commentPanel/{curPage}/{search}"})
    public String commentPanel(Model model,@PathVariable(value = "curPage",required = false)Integer curPage,Boolean isRedirect,@PathVariable(value = "search",required = false)String search){
        if(curPage==null ||curPage<1) curPage = 1;
        model.addAttribute("currentPage","comment");
        //查询所有评论
        Page<Comment> page = new Page<>(curPage,PageSize);
        QueryWrapper<Comment> wrapper = null;
        if(search!=null&&!search.isEmpty()){
            wrapper = new QueryWrapper<>();
            wrapper.like("context",search);
        }
        Page<Comment> commentList = commentMapper.selectPage(page,wrapper);
        pageHelper(model, (Page<T>) commentList);
        model.addAttribute("commentList",commentList.getRecords());
        if(isRedirect==null||!isRedirect)return "/admin/design";
        else return "redirect:/admin/commentPanel";
    }
    @RequestMapping(value = {"/admin/categoryPanel","/admin/categoryPanel/{curPage}","/admin/categoryPanel/{curPage}/{search}"})
    public String categoryPanel(Model model,@PathVariable(value = "curPage",required = false)Integer curPage,Boolean isRedirect,@PathVariable(value = "search",required = false)String search){
        if(curPage==null ||curPage<1) curPage = 1;
        model.addAttribute("currentPage","category");
        //查询所有分类
        Page<Category> page = new Page<>(curPage,PageSize);
        QueryWrapper<Category> wrapper = null;
        if(search!=null&&!search.isEmpty()){
            wrapper = new QueryWrapper<>();
            wrapper.like("parent_name",search);
        }
        Page<Category> categoryList = categoryMapper.selectPage(page,wrapper);
        pageHelper(model, (Page<T>) categoryList);
        model.addAttribute("categoryList",categoryList.getRecords());
        if(isRedirect==null||!isRedirect)return "/admin/design";
        else return "redirect:/admin/categoryPanel";
    }

    @Autowired
    UserRestController userRestController;
    //form表单添加
    @RequestMapping(value = "/user/addAddress")
    public String addAddress(Address address,Model model){
        model.addAttribute("currentPage","address");
        boolean flag = false;
        if(address.getIsDefault()==1){
            address.setIsDefault(0);
            flag = true;
        }
        addressMapper.insert(address);
        if(flag){
            userRestController.setDefaultAddress(JSONObject.parseObject(JSONObject.toJSONString(address)));
        }
        return addressPanel(model,null,true,null);
    }
    @RequestMapping(value = "/admin/createOrder")
    public String createOrder(Order order,Model model){
        model.addAttribute("currentPage","order");
        order.setBoundId(NanoIdUtils.randomNanoId());
        order.setOrderId(NanoIdUtils.randomNanoId(new SecureRandom(),OrderController.ALPHABET,12));
        order.setOrderTime(new Timestamp(new Date().getTime()));
        orderMapper.insert(order);
        return orderPanel(model,null,true,null,null);
    }
    @RequestMapping(value = "/admin/addCommentReq")
    public String createComment(Comment comment,Model model){
        model.addAttribute("currentPage","product");
        comment.setCreateTime(new Timestamp(new Date().getTime()));
        comment.setCommentId(NanoIdUtils.randomNanoId());
        commentMapper.insert(comment);
        return commentPanel(model,null,true,null);
    }


    //单物品修改请求
    @RequestMapping("/admin/modifyUserReq")
    public String modifyUserReq(User user,Model model){
        user.setPassword(SecureUtil.md5(user.getPassword())); //给密码加密
        userMapper.updateById(user);
        return userPanel(model,null,true,null,null);
    }
    @RequestMapping("/admin/modifyProductReq")
    public String modifyProductReq(Product product,Model model){
        productMapper.updateById(product);
        return productPanel(model,null,true,null);
    }
    @RequestMapping("/admin/modifyAddressReq")
    public String modifyAddressReq(Address address,Model model){
        address.setTelephone(address.getMobile());
        boolean flag = false;
        if(address.getIsDefault()==1){
            address.setIsDefault(0);
            flag= true;
        }
        addressMapper.updateById(address);
        if(flag){
            userRestController.setDefaultAddress(JSONObject.parseObject(JSONObject.toJSONString(address)));
        }
        model.addAttribute("currentPage","address");
        return addressPanel(model,null,true,null);
    }
    @RequestMapping("/admin/modifyOrderReq")
    public String modifyOrderReq(Order order,Model model){
        orderMapper.updateById(order);
        return orderPanel(model,null,true,null,null);
    }
    @RequestMapping("/admin/modifyCommentReq")
    public String modifyCommentReq(Comment comment,Model model){
        commentMapper.updateById(comment);
        return commentPanel(model,null,true,null);
    }

    //单物品删除请求
    @RequestMapping("/admin/delUser/{uid}")
    public String delSingleUser(@PathVariable("uid")String user_id,Model model){
        model.addAttribute("currentPage","user");
        List<String> userid = new ArrayList<>();
        userid.add(user_id);
        String msg = delUsers(userid);
        if(!msg.equals("操作成功")){
            System.out.println(msg);
        }
        return userPanel(model,null,true,null,null);
    }
    @RequestMapping("/admin/delProduct/{pid}")
    public String delSingleProduct(@PathVariable("pid")String pid,Model model){
        model.addAttribute("currentPage","product");
        List<String> productId = new ArrayList<>();
        productId.add(pid);
        String msg = delProducts(productId);
        if(!msg.equals("操作成功")){
            System.out.println(msg);
        }
        return userPanel(model,null,true,null,null);
    }
    @RequestMapping("/admin/delOrder/{oid}")
    public String delSingleOrder(@PathVariable("oid")String order_id,Model model){
        model.addAttribute("currentPage","order");
        List<String> orderIds = new ArrayList<>();
        orderIds.add(order_id);
        String msg = delOrders(orderIds);
        if(!msg.equals("操作成功")){
            System.out.println(msg);
        }
        return orderPanel(model,null,true,null,null);
    }
    @RequestMapping("/admin/delAddress/{addId}")
    public String delSingleAddress(@PathVariable("addId")Integer addId,Model model){
        model.addAttribute("currentPage","address");
        List<Integer> addIds = new ArrayList<>();
        addIds.add(addId);
        String msg = delAddress(addIds);
        if(!msg.equals("操作成功")){
            System.out.println(msg);
        }
        return addressPanel(model,null,true,null);
    }
    @RequestMapping("/admin/delComment/{commentId}")
    public String delSingleComment(@PathVariable("commentId")String commentId,Model model){
        model.addAttribute("currentPage","comment");
        List<String> commentIds = new ArrayList<>();
        commentIds.add(commentId);
        String msg = delComment(commentIds);
        if(!msg.equals("操作成功")){
            System.out.println(msg);
        }
        return commentPanel(model,null,true,null);
    }
    @RequestMapping("/admin/delCategory/{categoryId}")
    public String delSingleCategory(@PathVariable("categoryId")String categoryId,Model model){
        model.addAttribute("currentPage","category");
        List<String> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        String msg = delCategory(categoryIds);
        if(!msg.equals("操作成功")){
            System.out.println(msg);
        }
        return categoryPanel(model,null,true,null);
    }

    //批量删除操作
    @RequestMapping("/admin/delItems")
    @ResponseBody
    public String delItems(@RequestBody JSONObject json){
        String objType = json.getString("object");
        List<String> objectIds = json.getList("objectIds", String.class);
        switch (objType){
            case "order": return delOrders(objectIds);
            case "product": return delProducts(objectIds) ;
            case "user": return delUsers(objectIds) ;
            case "address": return delAddress(json.getList("objectIds",Integer.class)) ;
            case "commnet": return delComment(objectIds);
            default: return statCode.ErrorCode("object不存在");
        }
    }




    //添加物品页面
    @RequestMapping(value = "/admin/addUser")
    public String addUser(Model model){
        model.addAttribute("currentPage","user");
        return "/admin/insert";
    }
    @RequestMapping("/admin/addOrder")
    public String addOrder(Model model){
        model.addAttribute("currentPage","order");
        return "/admin/insert";
    }
    @RequestMapping("/admin/addProduct")
    public String addProduct(Model model){
        model.addAttribute("currentPage","product");
        return "/admin/insert";
    }    @RequestMapping("/admin/addAddress")
    public String addAddress(Model model){
        model.addAttribute("currentPage","address");
        return "/admin/insert";
    }    @RequestMapping("/admin/addComment")
    public String addComment(Model model){
        model.addAttribute("currentPage","comment");
        return "/admin/insert";
    }
    @RequestMapping("/admin/addCategory")
    public String addCategoryPage(Model model){
        model.addAttribute("currentPage","category");
        return "/admin/insert";
    }


    //修改物品页面
    @RequestMapping("/admin/modifyUser/{uid}")
    public String modifyUser(@PathVariable("uid")String user_id,Model model){
        model.addAttribute("currentPage","user");
        User user = userMapper.selectById(user_id);
        model.addAttribute("user",user);
        return "/admin/modify";
    }
    @RequestMapping("/admin/modifyOrder/{oid}")
    public String modifyOrderPanel(@PathVariable("oid")String oid,Model model){
        model.addAttribute("currentPage","order");
        Order order = orderMapper.queryOrderById(oid);
        model.addAttribute("order",order);
        return "/admin/modify";
    }
    @RequestMapping("/admin/modifyProduct/{pid}")
    public String modifyProductPanel(@PathVariable("pid")String pid,Model model){
        model.addAttribute("currentPage","product");
        Product product = productMapper.selectById(pid);
        model.addAttribute("product",product);
        return "/admin/modify";
    }
    @RequestMapping("/admin/modifyAddress/{addId}")
    public String modifyAddressPanel(@PathVariable("addId")Integer addId,Model model){
        model.addAttribute("currentPage","address");
        Address address = addressMapper.selectById(addId);
        model.addAttribute("address",address);
        return "/admin/modify";
    }
    @RequestMapping("/admin/modifyComment/{commentId}")
    public String modifyCommentPanel(@PathVariable("commentId")String commentId,Model model){
        model.addAttribute("currentPage","comment");
        Comment comment = commentMapper.selectById(commentId);
        model.addAttribute("comment",comment);
        return "/admin/modify";
    }
    @RequestMapping("/admin/modifyCategory/{categoryId}")
    public String modifyCategoryPanel(@PathVariable("categoryId")String categoryId,Model model){
        model.addAttribute("currentPage","category");
        Category category = categoryMapper.selectById(categoryId);
        model.addAttribute("category",category);
        return "/admin/modify";
    }
    //备份页面
    @RequestMapping(value = {"/admin/backup/{curPage}","/admin/backup"})
    public String backupPage(@PathVariable(value = "curPage",required = false)Integer curPage,Model model){
        File[] ls = FileUtil.ls(BackUpDataBaseManager.sqlPath);
        List<File> files = new ArrayList<>(ls.length);
        for (File file : ls) {
            if(file.isFile()){
                files.add(file);
            }
        }
        model.addAttribute("sqlPath",BackUpDataBaseManager.sqlPath);
        model.addAttribute("files",files);
        BigDecimal totalSize = new BigDecimal(0);
        for (File file : files) {
            totalSize = totalSize.add(new BigDecimal(file.length()));
        }
        totalSize = totalSize.divide(new BigDecimal(1024*1024));
        totalSize = totalSize.setScale(3, RoundingMode.HALF_UP);
        model.addAttribute("totalSize",totalSize.toString()+"MB");
        return "admin/backup";
    }

    /**
     * 用于匹配前端的分页参数
     * @param model 传递模型层
     * @param resultList Mp分页插件的PageList
     */
    private void pageHelper(Model model, Page<T> resultList){
        model.addAttribute("Totals",resultList.getTotal());
        TotalPages= Math.toIntExact(resultList.getPages());
        if(TotalPages==0) TotalPages = 1;
        int curPage = Math.toIntExact(resultList.getCurrent());//覆盖无效的curPage
        model.addAttribute("curPage", curPage);
        if(curPage >TotalPages) curPage = TotalPages;
        if(curPage -((PageLength-1)/2)<=0){
            Findex = 1;
            Lindex = Math.min(PageLength, TotalPages);
        }else {
            Findex = curPage -((PageLength-1)/2);
            Lindex = curPage +((PageLength-1)/2);
            if(Lindex > TotalPages) Lindex = TotalPages;
        }
        model.addAttribute("Findex",Findex);
        model.addAttribute("Lindex",Lindex);
        model.addAttribute("TotalPages",TotalPages);
    }


    //删除物品的私有方法
    private String delOrders(List<String> orderIds){
        int result=0;
        for (String id:orderIds) {
            result+=orderMapper.deleteById(id);
        }
        if(result!=orderIds.size()) return statCode.ErrorCode("删除操作不一致");
        else return statCode.PassCodeOnly("操作成功");
    }
    private String delProducts(List<String> productIds){
        int result=0;
        for (String id:productIds) {
            result+=productMapper.deleteById(id);
        }
        if(result!=productIds.size()) return statCode.ErrorCode("删除操作不一致");
        else return statCode.PassCodeOnly("操作成功");
    }
    private String delUsers(List<String> userIds){
        int result=0;
        for (String id:userIds) {
            result+=userMapper.deleteById(id);
        }
        if(result!=userIds.size()) return statCode.ErrorCode("删除操作不一致");
        else return statCode.PassCodeOnly("操作成功");
    }
    private String delAddress(List<Integer> addIds){
        int result=0;
        for (Integer id:addIds) {
            result+=addressMapper.delAddByAddIdByLogic(id);
        }
        if(result!=addIds.size()) return statCode.ErrorCode("删除操作不一致");
        else return statCode.PassCodeOnly("操作成功");
    }
    private String delComment(List<String> commentIds){
        int result=0;
        for (String id:commentIds) {
            result+=commentMapper.deleteById(id);
        }
        if(result!=commentIds.size()) return statCode.ErrorCode("删除操作不一致");
        else return statCode.PassCodeOnly("操作成功");
    }
    private String delCategory(List<String> categoryIds){
        int result=0;
        for (String id:categoryIds) {
            result+=categoryMapper.deleteById(id);
        }
        if(result!=categoryIds.size()) return statCode.ErrorCode("删除操作不一致");
        else return statCode.PassCodeOnly("操作成功");
    }
}
