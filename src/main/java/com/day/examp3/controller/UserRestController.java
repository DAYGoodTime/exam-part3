package com.day.examp3.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.pojo.Collection;
import com.day.examp3.servicesImpl.OrderServicesImpl;
import com.day.examp3.servicesImpl.UserServicesImpl;
import com.day.examp3.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

import static com.day.examp3.controller.OrderController.ALPHABET;

/**
 * 用于处理用户方面的ajax请求
 */
@RestController()
public class UserRestController {

    public static final String servehost = "http://localhost:8080";

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserServicesImpl userServices;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    LocationMapper locationMapper;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderServicesImpl orderServices;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    CollectionMapper collectionMapper;

    @Autowired
    StatCode statCode;//状态码工具类

    @Autowired
    AESUtil aesUtil;

    @PostMapping("/user/addAddress")
    @ResponseBody
    public String addAddress(@RequestBody JSONObject object){
        Address address = new Address();
        initAddressFromJson(object,address);
        try{
            if(address.getIsDefault()==1){
                setDefaultAddress(object);
            }
        }catch (NullPointerException e){
            address.setIsDefault(0);
        }
        if(addressMapper.queryAddressCountByUid(address.getUserId())==0){
            address.setIsDefault(1);
        }
        if(addressMapper.insert(address)!=1){
            statCode.ErrorCode("上传到数据库失败");
        }else {
            return statCode.PassCodeOnly("操作成功");
        }
        return statCode.ErrorCode("未知错误");
    }
    private void initAddressFromJson(JSONObject object,Address address) {
        address.setUserId(object.getString("uid"));
        address.setReceiveName(object.getString("name"));
        String province = locationMapper.getLocationsById(object.getInteger("province")).getName();
        String city = locationMapper.getLocationsById(object.getInteger("city")).getName();
        String county = locationMapper.getLocationsById(object.getInteger("county")).getName();
        if(city==null || city.isEmpty()){
            address.setProvince(province);
        }else if(county==null || county.isEmpty()){
            address.setProvince(province+" "+city);
        }else {
            address.setProvince(province+" "+city+" "+county);
        }
        address.setAddress(object.getString("address"));
        address.setMobile(object.getString("phone"));
        address.setZipcode(object.getString("zipcode"));
    }
    public String setDefaultAddress(JSONObject object){
        String user_id = object.getString("uid");
        if(user_id==null){
            user_id = object.getString("userId");
        }
        Integer addId = object.getInteger("addId");
        if(addId==null){
            addId = object.getInteger("receiveAddressId");
        }
        Address oldDefaultAddress;
        try{
            oldDefaultAddress = addressMapper.queryDefaultUserAddress(user_id);
        }catch (Throwable t){
            oldDefaultAddress = null;
        }
        Address newDefaultAddress = addressMapper.queryUserAddressById(user_id,addId);
        if(newDefaultAddress==null){
            return statCode.ErrorCode("新地址不存在");
        }
        //出现了设置同一个默认地址的情况
        if(oldDefaultAddress!=null&& Objects.equals(oldDefaultAddress.getReceiveAddressId(),newDefaultAddress.getReceiveAddressId())){
            return statCode.ErrorCode("该地址已经是默认地址");
        }
        int res = 0;
        if(oldDefaultAddress==null) {
            res++;
        }else {
            oldDefaultAddress.setIsDefault(0);
            res+= addressMapper.updateById(oldDefaultAddress);
        }
        newDefaultAddress.setIsDefault(1);
        res+= addressMapper.updateById(newDefaultAddress);
        if(res!=2){
            //操作失败
            return statCode.ErrorCode("更新地址失败");
        }
        return statCode.PassCodeOnly("更新成功");
    }

    @RequestMapping("/user/resetPwd/{uid}")
    public String rest_userPwd(@PathVariable("uid")String user_id,@RequestBody JSONObject jsonObject,
                               HttpSession session
    ){
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("capt");
        if(!shearCaptcha.verify(jsonObject.getString("code"))) return statCode.ErrorCode("验证码错误");
        User user = userMapper.selectById(user_id);
        String oldPwd = jsonObject.getString("oldPassword");
        String newPwd = jsonObject.getString("newPassword");
        String userEmail = jsonObject.getString("email");
        if(userEmail!=null&&!userEmail.isEmpty()){
            if(userEmail.equals(user.getEmail())){
                String uidhex = aesUtil.encryptHex(user.getUserId());
                String context = "<h1>重置密码</h1>\n" +
                        "    <p>点击链接既可重置你的密码</p>\n" +
                        "    <a href="+servehost+"/resetPwd/"+uidhex+">点击此处重置您的密码</a>\n" +
                        "    <br><span>如果你没有需要重置密码,无需理会本邮件</span>";
                MailUtil.send(user.getEmail(), "重置密码",context, true);
                return statCode.PassCode("邮箱发送成功", DesensitizedUtil.email(user.getEmail()));
            }else {
                return statCode.ErrorCode("用户邮箱不正确");
            }
        }else {
            if(SecureUtil.md5(oldPwd).equals(user.getPassword())){
                user.setPassword(SecureUtil.md5(newPwd));
                if(userMapper.updateById(user)>=1){
                    return statCode.PassCodeOnly("操作成功");
                }else {
                    return statCode.ErrorCode("修改失败,原因未知");
                }
            }else {
                return statCode.ErrorCode("旧密码不正确");
            }
        }
    }
    @RequestMapping("/updateAddress")
    public String updateAddress(@RequestBody JSONObject object){
        Address address = new Address();
        String method = object.getString("method");
        if(method == null){
            return statCode.ErrorCode("方法参数无效");
        } else if (method.equals("fullUpdate")) { //完整地址更新
            return fullUpdateAddress(object,address);
        } else if (method.equals("setDefault")) { //设置默认地址更新
            return setDefaultAddress(object);
        }
        return statCode.ErrorCode("无匹配的方法");
    }
    @RequestMapping("/delAddress")
    public String delAddress(@RequestBody JSONObject object){
        String user_id = object.getString("user_id");
        Integer addId = object.getInteger("addId");
        if(user_id==null || user_id.isEmpty() || addId==null){
            return statCode.ErrorCode("地址id或用户id为空");
        }
        if(addressMapper.delAddByUserIdByLogic(user_id,addId)!=1){
            return statCode.ErrorCode("删除失败");
        }
        return statCode.PassCodeOnly("操作成功");
    }
    @PostMapping("/queryAddress/{addId}")
    public String queryAddress(@PathVariable("addId")Integer addId,HttpSession session){
        String user_id = (String) session.getAttribute("user_id");
        Address address = addressMapper.queryUserAddressById(user_id, addId);
        if(address==null){
            //TODO 查询失败操作
        }
        return JSONObject.toJSONString(address);
    }

    public String fullUpdateAddress(JSONObject object,Address address){
        initAddressFromJson(object,address);
        address.setReceiveAddressId(object.getLong("addId"));
        try{
            if(address.getIsDefault()==1){
                setDefaultAddress(object);
            }
        }catch (NullPointerException e){
            address.setIsDefault(0);
        }
        if(addressMapper.updateById(address)!=1){
            return statCode.ErrorCode("更新数据失败");
        }else return statCode.PassCodeOnly("成功");
    }

    //购物车功能
    @RequestMapping(value = "/addToCart",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public String addToCart(@RequestBody JSONObject jsonParam,HttpSession session){
        String user_id = jsonParam.getString("user_id");
        String product_id = jsonParam.getString("product_id");
        Integer proAmount = jsonParam.getInteger("proAmount");
        if(userServices.addToUserCart(user_id,product_id,proAmount)){
            session.setAttribute("userShoppings",userMapper.getUserShoppingCount(user_id));//更新session
            return statCode.PassCodeOnly("添加成功");
        }
        return statCode.ErrorCode("添加失败");
    }
    @RequestMapping(value = "/delCart",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public String delCart(@RequestBody JSONObject json,HttpSession session){
        String user_id = json.getString("user_id");
        List<JSONObject> objList = json.getList("products",JSONObject.class);
        if(objList==null || objList.isEmpty()){
            //执行单次删除
            String product_id = json.getString("product_id");
            if(userServices.delProductToUserCart(user_id,product_id)){
                session.setAttribute("userShoppings",userMapper.getUserShoppingCount(user_id));
                return statCode.PassCodeOnly("单物品操作成功");
            }

            else return statCode.ErrorCode("单物品操作失败");
        }else
        {//执行批量删除操作
            List<String> pids = new ArrayList<>();
            for (JSONObject obj:objList) {
                pids.add(obj.getString("pid"));
            }
            for (String pid:pids) {
                if(!userServices.delProductToUserCart(user_id,pid)) return statCode.ErrorCode("删除失败");
            }
            session.setAttribute("userShoppings",userMapper.getUserShoppingCount(user_id));
            return statCode.PassCodeOnly("操作成功");
        }
    }

    //个人信息修改
    @RequestMapping(value = "/user/updateBaseInfo",method = RequestMethod.POST)
    public String updateUserInfo(@RequestBody JSONObject json,HttpServletRequest request,HttpServletResponse response) {
        String user_id = json.getString("userId");
        User oldUser = userMapper.selectById(user_id);
        String username = json.getString("username");
        String birth =json.getString("birth");
        String sex = json.getString("sex");

        oldUser.setNickName(username);
        //日期处理
        Timestamp data = DataUtil.StringToStamp(birth);
        oldUser.setBirth(data);
        oldUser.setSex(sex);
        if(userMapper.updateById(oldUser)!=1){
            return statCode.ErrorCode("上传用户信息失败");
        }
        //更新cookie和session
        request.getSession().setAttribute("username",username);
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("username")){
                cookie.setValue(username);
                response.addCookie(cookie);
                break;
            }
        }
        return statCode.PassCodeOnly("操作成功");
    }
    @RequestMapping(value = "/user/updateAreaReq",method = RequestMethod.POST)
    public String UserUpdateArea(@RequestBody JSONObject json){
        String user_id = json.getString("userId");
        String area = json.getString("area");
        if(area==null || area.isEmpty()){
            return statCode.ErrorCode("地址栏为空");
        }
        User olduser = userMapper.selectById(user_id);
        olduser.setArea(area);
        if(userMapper.updateById(olduser)!=1){
            return statCode.ErrorCode("更新用户数据失败");
        }
        return statCode.PassCodeOnly("操作成功");
    }
    @RequestMapping(value = "/user/updateAvatar",method = RequestMethod.POST)
    public String UserUpdateAvatar(MultipartFile file, HttpSession session){
        String time = DataUtil.DataToString(new Date());
        if(file==null) return statCode.ErrorCode("文件为空");
        User user = userMapper.selectById((Serializable) session.getAttribute("user_id"));
        if(user==null) return statCode.ErrorCode("用户查询失败");
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.length() - 4);
        if (!prefix.equalsIgnoreCase(".png")) return statCode.ErrorCode("只支持png文件");
        //通常需要修改图片的名字（防止重复）
        String newName = session.getAttribute("user_id") + "-" + NanoIdUtils.randomNanoId()+"-"+time+prefix;
        String returnUrl = Cos.upLoadImgs(file, newName);
        user.setHeadPic(returnUrl);
        int res = userMapper.updateById(user);
        if(res!=1) return statCode.ErrorCode("头像更新失败");
        // 头像刷新
        session.setAttribute("user_img",returnUrl);
        return statCode.PassCodeOnly("操作成功");
    }


    @RequestMapping("/user/buyNow")
    public String buyNow(@RequestBody JSONObject object){
        Order order = new Order();
        String user_id = object.getString("user_id");
        order.setOrderId(NanoIdUtils.randomNanoId(new SecureRandom(),ALPHABET,12));
        order.setAmount(object.getInteger("pamount"));
        order.setUserId(user_id);
        String pid = object.getString("pid");
        order.setProductId(pid);
        Product product = productMapper.selectById(pid);
        order.setTotalPrice(product.getFixedPrice());
        //查询用户默认的配送地址
        Address defaultAddress = addressMapper.queryDefaultUserAddress(user_id);
        order.setReceiveAddress(defaultAddress);
        order.setReceiveAddressId(defaultAddress.getReceiveAddressId());
        //设置初始状态
        order.setStatus("待支付");
        //设置默认的支付方式
        order.setPaymentId(1);
        //设置订单时间
        order.setOrderTime(new Timestamp(new Date().getTime()));
        String BoundId = NanoIdUtils.randomNanoId();
        order.setBoundId(BoundId);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        if(!orderServices.addBatchOrders(orders)){
            return statCode.ErrorCode("订单上传失败");
        }
        Map<String,String> map = new HashMap<>();
        map.put("bid",BoundId);
        return statCode.PassCode("操作成功",map);
    }

    @RequestMapping("/restPassword")
    public String toForget(@RequestBody JSONObject json,HttpSession session) {
        ShearCaptcha capt = (ShearCaptcha) session.getAttribute("capt");
        String code = json.getString("code");
        if (code == null || code.isEmpty() || !capt.verify(code)) {
            return statCode.ErrorCode("验证码错误");
        }
        String email = json.getString("email");
        String regex_email = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"; //邮箱匹配
        if (email!=null &&email.matches(regex_email)) {
            User user = userMapper.queryUserByEmail(email);
            if (user == null) {
                return statCode.ErrorCode("该用户不存在");
            }
            String uidhex = aesUtil.encryptHex(user.getUserId());
            String context = "<h1>重置密码</h1>\n" +
                    "    <p>点击链接既可重置你的密码</p>\n" +
                    "    <a href="+servehost+"/resetPwd/"+uidhex+">点击此处重置您的密码</a>\n" +
                    "    <br><span>如果你没有需要重置密码,无需理会本邮件</span>";

            MailUtil.send(user.getEmail(), "重置密码",context, true);
            return statCode.PassCode("修改成功",DesensitizedUtil.email(user.getEmail()));//信息脱敏处理
        } else {
            return statCode.ErrorCode("邮箱格式错误");
        }
    }
    @RequestMapping("/restPasswordByEmailReq")
    public String resetPwd(@RequestBody JSONObject json){
        String pwd = json.getString("password");
        String confirm_pwd = json.getString("confirmPwd");
        if(!pwd.equals(confirm_pwd)) return statCode.ErrorCode("密码与确认密码不一致");
        String user_id = json.getString("uid");
        User user = userMapper.selectById(user_id);
        if(user==null) return statCode.ErrorCode("用户不存在");
        user.setPassword(SecureUtil.md5(confirm_pwd));
        if(userMapper.updateById(user)!=1) return statCode.ErrorCode("修改用户密码失败");
        return statCode.PassCodeOnly("修改成功");
    }

    //评论系统
    @RequestMapping("/user/addComment")
    public String addComment(@RequestBody JSONObject json){
        Map map = json.getObject("obj",Map.class);
        String uid = (String) map.get("uid");
        String pid = (String) map.get("pid");
        String text = (String) map.get("comment");
        String like = (String) map.get("likes");
        if(like.isEmpty()){
            like = "0";
        }
        Integer likes = Integer.parseInt(like);
        String comment_id = NanoIdUtils.randomNanoId();
        Comment comment = new Comment(comment_id,uid,null,pid,null,text,new Timestamp(new Date().getTime()),likes);
        commentMapper.insert(comment);
        List<Order> orders = orderMapper.queryOrderNeedProd(uid,pid);
        orders.get(0).setStatus("已完成");
        orders.get(0).setCommentId(comment_id);
        orderMapper.updateById(orders.get(0));
        return statCode.PassCodeOnly("添加成功");
    }
    @RequestMapping("/queryComment")
    public String queryComment(@RequestBody JSONObject json) {
        String comment_id = json.getString("comment_id");
        if (comment_id == null || comment_id.isEmpty()) return statCode.ErrorCode("评论id为空");
        Comment comment = commentMapper.selectById(comment_id);
        if (comment == null) return statCode.ErrorCode("评论不存在");
        return statCode.PassCode("查询成功", comment);
    }


    //收藏功能
    @RequestMapping("/user/addCollection")
    public String addCollection(@RequestBody JSONObject json){
        String user_id = json.getString("user_id");
        String pid = json.getString("pid");
        if(pid==null||user_id==null || pid.isEmpty() ||user_id.isEmpty()) return statCode.ErrorCode("用户id或商品id为空");
        Collection collection = new Collection(null,user_id,pid,new Timestamp(new Date().getTime()));
        if(collectionMapper.insert(collection)!=1) return statCode.ErrorCode("添加收藏失败");
        return statCode.PassCodeOnly("操作成功");
    }
    @RequestMapping("/user/delCollection")
    public String delCollection(@RequestBody JSONObject json){
        String user_id = json.getString("user_id");
        String pid = json.getString("pid");
        if(pid==null||user_id==null || pid.isEmpty() ||user_id.isEmpty()) return statCode.ErrorCode("用户id或商品id为空");
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",user_id);
        map.put("product_id",pid);
        int result = collectionMapper.deleteByMap(map);
        if(result!=1){
            return statCode.ErrorCode("删除收藏失败");
        }
        return statCode.PassCodeOnly("操作成功");
    }
    @RequestMapping("/user/queryCollection")
    public String queryCollection(@RequestBody JSONObject json){
        String user_id = json.getString("user_id");
        String pid = json.getString("pid");
        if(pid==null||user_id==null || pid.isEmpty() ||user_id.isEmpty()) return statCode.ErrorCode("用户id或商品id为空");
        JSONObject result = new JSONObject();

        if(collectionMapper.queryCollection(user_id,pid)!=1) {
            result.put("isCollected","false");
            return statCode.PassCode("操作成功",result);
        }else {
            result.put("isCollected","true");
        }
        return statCode.PassCode("操作成功",result);
    }

}
