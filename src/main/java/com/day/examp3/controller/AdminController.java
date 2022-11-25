package com.day.examp3.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

/**
 * 用于处理ajax请求的Admin控制层
 */
@RestController
@CrossOrigin
public class AdminController {
    public static final int MAX_FILE_SIZE = 5242880;//TODO 文件上传大小匹配config
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserController userController;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    StatCode statCode;


    /**
     * 获取系统信息
     */
    @RequestMapping("/admin/getSysInfo")
    @ResponseBody
    public String getSysInfo(){
        Map<String,String> disk = Monitor.getDisk();
        Map<String,String> mem = Monitor.getMem();
        Map<String,String> cpu = Monitor.getCpuInfo();
        JSONObject json = new JSONObject();
        json.put("disk",disk);
        json.put("mem",mem);
        json.put("cpu",cpu);
        return statCode.PassCode("获取成功",json);
    }
    @Autowired
    DynamicConfigUtil dynamicConfig;

    /**
     * 进行动态更新配置文件
     * @param json 配置文件
     */
    @RequestMapping("/admin/updateSettingReq")
    public String updateSetting(@RequestBody JSONObject json){
        Map<String,Object> mapObj = JSONObject.parseObject(json.toJSONString(),Map.class);
        return dynamicConfig.updateEnvironment(mapObj);
    }


    /**
     * 管理员的登录请求
     * @param json 表单文件
     */
    @RequestMapping("/adminLoginReq")
    public String loginReq(@RequestBody JSONObject json,HttpServletRequest request, HttpServletResponse response){
        String account = json.getString("username");
        String password = SecureUtil.md5(json.getString("pwd"));
        String rememberMe = json.getString("rememberMe");
        String reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";//邮箱检测
        User user;
        if(account==null||password==null){
            return statCode.ErrorCode("账号密码为空");
        }
        if(account.matches(reg)){
           user = userMapper.getLoginUserByEmail(account,password);
        }else {
            user = userMapper.getLoginUserByNickName(account,password);
        }
        if(user==null){
            return statCode.ErrorCode("账号密码错误");
        }
        if(user.getIsAdmin()!=1) return statCode.ErrorCode("权限不足");
        Integer numbers = userMapper.getUserShoppingCount(user.getUserId());
        if(rememberMe!= null && rememberMe.equals("true")){
            Cookie userid = new Cookie("user_id",user.getUserId());
            userid.setMaxAge(24*60*60);
            Cookie username = new Cookie("username",user.getNickName());
            username.setMaxAge(24*60*60);
            Cookie user_img = new Cookie("user_img",user.getHeadPic());
            user_img.setMaxAge(24*60*60);
            Cookie user_isAdmin = new Cookie("isAdmin",user.getHeadPic());
            user_img.setMaxAge(24*60*60);
            Cookie user_shoppings = new Cookie("userShoppings",numbers.toString());
            user_img.setMaxAge(24*60*60);
            response.addCookie(userid);
            response.addCookie(username);
            response.addCookie(user_img);
            response.addCookie(user_isAdmin);
            response.addCookie(user_shoppings);
        }
        HttpSession session = request.getSession();
        session.setAttribute("username",user.getNickName());
        session.setAttribute("user_id",user.getUserId());
        session.setAttribute("user_img",user.getHeadPic());
        session.setAttribute("isAdmin",user.getIsAdmin());
        session.setAttribute("userShoppings",numbers);
        //TODO 登录时间,登录IP (待完善)
        //登录Ip获取,最基础的方式,分布式ip需要使用其他方法
        String loginIp = request.getRemoteAddr();
        user.setLastLoginIp(loginIp);
        //设置登录时间
        user.setLastLoginTime(new Timestamp(new Date().getTime()));
        if(userMapper.updateById(user)!=1) return statCode.ErrorCode("数据上传失败");
        return statCode.PassCodeOnly("登录成功");
    }

    /**
     * 添加商品请求
     */
    @RequestMapping(value = "/admin/addProReq")
    public String createProduct(HttpServletRequest request){
        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request); //表单参数
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");//表单文件
        Product product = new Product();
        String pid = IdUtil.getSnowflakeNextIdStr();
        product.setProductId(pid);
        if(files.size()==0){
            return statCode.ErrorCode("必须传入图片");
        }else {
            MultipartFile file = files.get(0);
            //图片操作
            if(file.getSize()>MAX_FILE_SIZE) return statCode.ErrorCode("文件大小不能超过5MB");
            String fileName = file.getOriginalFilename();
            String prefix = fileName.substring(fileName.length() - 4);
            if(!prefix.equalsIgnoreCase(".png")) return statCode.ErrorCode("只支持png格式的图片");
            //通常需要修改图片的名字（防止重复）
            String newName = pid + "-" + NanoIdUtils.randomNanoId(new SecureRandom(),null,4)+prefix;
            String url = Cos.upLoadImgs(file,newName);
            if(url==null) return statCode.ErrorCode("上传图片失败");
            product.setPicture(url);
        }
        String name = params.getParameter("name");
        String fixedPrice = params.getParameter("fixedPrice");
        String keywords = params.getParameter("keywords");
        String catalog = params.getParameter("catalog");
        if(catalog!=null){
            product.setCatalog(catalog);
        }
        String storage = params.getParameter("storage");
        String description = params.getParameter("description");
        product.setName(name);
        product.setFixedPrice(new BigDecimal(fixedPrice));
        product.setKeywords(keywords);
        product.setStorage(Integer.parseInt(storage));
        product.setDescription(description);
        if(productMapper.insert(product)!=1) return statCode.ErrorCode("添加失败");
        return statCode.PassCodeOnly("添加成功");
    }
    /**
     * 添加分类请求
     * @param json 表单文件
     */
    @RequestMapping("/admin/addCategoryReq")
    public String addCategory(@RequestBody JSONObject json){
        Category category = json.to(Category.class);
        if(category.getParentCategory().isEmpty()||category.getParentName().isEmpty()) return statCode.ErrorCode("父类id和name为空");
        if(category.getChildrenCategory().isEmpty() && !category.getChildrenName().isEmpty()) return statCode.ErrorCode("子类id和name不能只填一个");
        if(!category.getChildrenCategory().isEmpty() && category.getChildrenName().isEmpty()) return statCode.ErrorCode("子类id和name不能只填一个");
        if(category.getChildrenCategory().isEmpty() && category.getChildrenName().isEmpty()){
            category.setChildrenCategory(null);
            category.setChildrenName(null);
        }
        if(categoryMapper.insert(category)!=1) return statCode.ErrorCode("添加失败");
        return statCode.PassCodeOnly("添加成功");
    }
    /**
     * 修改分类请求
     * @param json 表单文件
     */
    @RequestMapping("/admin/modifyCategoryReq")
    public String modifyCategoryReq(@RequestBody JSONObject json){
        Category category = json.getObject("obj",Category.class);
        if(category==null||category.getCategoryId()==0) return statCode.ErrorCode("对象为空");
        if(category.getChildrenCategory().isEmpty()){
            if(!(category.getChildrenCategory().isEmpty()&& category.getChildrenName().isEmpty())) return statCode.ErrorCode("子类id和name不一致为空");
            category.setChildrenCategory(null);
            category.setChildrenName(null);
        }
        if(categoryMapper.updateById(category)!=1) return statCode.ErrorCode("更新失败");
        return statCode.PassCodeOnly("更新成功");
    }

    //添加操作
    //管理面板添加用户接口
    @RequestMapping(value = "/admin/addUserReq")
    public String addUser(HttpServletRequest request){
        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request); //表单参数
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");//表单文件
        User user =new User();
        String user_id = NanoIdUtils.randomNanoId();//生成Id
        if(files.size()==0){
            user.setHeadPic("http://localhost:8080/static/userAvr/tx.png");//设置默认头像
        }else {
            MultipartFile file = files.get(0);
            //图片操作
            if(file.getSize()>MAX_FILE_SIZE) return statCode.ErrorCode("文件大小不能超过5MB");
            String fileName = file.getOriginalFilename();
            String prefix = fileName.substring(fileName.length() - 4);
            if(!prefix.equalsIgnoreCase(".png")) return statCode.ErrorCode("只支持png格式的图片");
            //通常需要修改图片的名字（防止重复）
            String newName = user_id + "-" + NanoIdUtils.randomNanoId()+"-"+DataUtil.DataToString(new Date())+prefix;
            String url = Cos.upLoadImgs(file,newName);
            if(url==null) return statCode.ErrorCode("上传图片失败");
            user.setHeadPic(url);
        }
        String nickName = params.getParameter("nickName");
        String password = params.getParameter("password");
        String sex = params.getParameter("sex");
        String email = params.getParameter("email");
        String phone = params.getParameter("phone");
        String area = params.getParameter("area");
        Integer isAdmin = Integer.parseInt(params.getParameter("isAdmin"));
        String data = params.getParameter("formBirth");
        user.setUserId(user_id);
        user.setNickName(nickName);
        user.setSex(sex);
        user.setEmail(email);
        user.setPhone(phone);
        user.setArea(area);
        user.setIsAdmin(isAdmin);
        user.setPassword(SecureUtil.md5(password));
        user.setBirth(DataUtil.StringToStamp(data));
        if(userMapper.insert(user)!=1){
            // 添加用户失败
            return statCode.ErrorCode("添加用户失败");
        }
        return statCode.PassCodeOnly("成功");
    }


    @Autowired
    BackUpDataBaseManager backUpDataBaseManager;
    //备份操作
    @RequestMapping("/admin/backupNow")
    public String backupNow(){
        if(backUpDataBaseManager.backUpDB()){
            return statCode.PassCodeOnly("操作成功");
        }else {
            return statCode.ErrorCode("备份失败");
        }
    }
    @RequestMapping("/admin/recoveryReq")
    public String recovery(@RequestBody JSONObject json){
        String fileName = json.getString("fileName");
        if(fileName==null||fileName.isEmpty()){
            return statCode.ErrorCode("文件名为空");
        }
        if(backUpDataBaseManager.recoveryDB(fileName)){
            return statCode.PassCodeOnly("恢复成功");
        }else {
            return statCode.ErrorCode("恢复失败");
        }
    }
    @RequestMapping("/admin/delSqlBKReq")
    public String delBk(@RequestBody JSONObject json){
        List<String> fileNames = json.getList("files",String.class);
        if(fileNames.isEmpty()){
            String fileName = json.getString("fileName");
            if(fileName==null||fileName.isEmpty()){
                return statCode.ErrorCode("文件名为空");
            }
            File file = new File(BackUpDataBaseManager.sqlPath+fileName);
            if(!file.exists()){
                return statCode.ErrorCode("文件不存在");
            }
            FileUtil.del(file);
        }else {
            for (String fileName : fileNames) {
                if(fileName==null||fileName.isEmpty()){
                    return statCode.ErrorCode("文件名为空");
                }
                File file = new File(BackUpDataBaseManager.sqlPath+fileName);
                if(!file.exists()){
                    return statCode.ErrorCode("文件不存在");
                }
                FileUtil.del(file);
            }
        }
        return statCode.PassCodeOnly("删除成功");
    }
}
