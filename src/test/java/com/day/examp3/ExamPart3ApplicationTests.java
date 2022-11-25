package com.day.examp3;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.setting.dialect.Props;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.day.examp3.config.OwnConfig;
import com.day.examp3.mapper.*;
import com.day.examp3.pojo.*;
import com.day.examp3.pojo.Collection;
import com.day.examp3.servicesImpl.CategoryServicesImpl;
import com.day.examp3.servicesImpl.OrderServicesImpl;
import com.day.examp3.servicesImpl.ProductServicesImpl;
import com.day.examp3.utils.BackUpDataBaseManager;
import com.day.examp3.utils.NanoIdUtils;
import com.day.examp3.utils.StatCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

@SpringBootTest
class ExamPart3ApplicationTests {

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
    ConfigurableEnvironment  environment;
    @Autowired
    OwnConfig ownConfig;

    @Autowired
    BackUpDataBaseManager backUpDataBaseManager;


    @Test
    void sys(){
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        double used = cpuInfo.getUsed();
        System.out.println(used);
        System.out.println(new DecimalFormat("#.##%").format(used));
    }

    @Test
    void category(){
        Page<Category> page = new Page<>(1,5);
        Page<Category> categoryList = categoryMapper.selectPage(page,null);
        List<Category> records = categoryList.getRecords();
        for (Category category : records) {
            System.out.println(category.toString());
        }
    }

    @Test
    void DB_backup(){
        backUpDataBaseManager.recoveryDB("2022-11-09-examp3.gz");
    }

    @Test
    void DB_b() throws IOException {
        File file = new File(BackUpDataBaseManager.sqlPath+"2022-11-09-examp3.gz");
        System.out.println(file.length());
        System.out.println(Files.readAllBytes(Paths.get(BackUpDataBaseManager.sqlPath+"2022-11-09-examp3.gz")).length);
    }

    @Test
    void config() throws IllegalAccessException {
        Class<OwnConfig> clz = OwnConfig.class;
        Map<String,String> settings = new HashMap<>();
        for (Field field:clz.getDeclaredFields()) {//获取配置内容
            field.setAccessible(true);
            settings.put(field.getName(),String.valueOf(field.get(ownConfig)));
        }
        for (Map.Entry<String,String> entry:settings.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }


    @Test
    void order(){
        List<Order> orderList = orderMapper.getFullOrdersByUserId("vinsiyZ72k");
        Order order = orderList.get(0);
        System.out.println(orderList.contains(order));
    }

    @Test
    void contextLoads(){
        long id = IdUtil.getSnowflakeNextId();
        System.out.println(id);
    }

    @Test
    void user(){
        User user = userMapper.selectById(1);
        System.out.println(user.toString());
        user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
        userMapper.updateById(user);
    }

    @Autowired
    private ServletContext servletContext;
    @Test
    void mail() {
        System.out.println(servletContext.getContext("/login"));
    }

    @Test
    void addproduct(){
//        for (int i = 1; i <=8; i++) {
//
//        }
        Product product = new Product();
        product.setName("ROG Strix GeForce RTX® 4090 OC 24GB");
        product.setPicture("/static/img/computer/card2.png");
        product.setKeywords("card");
        product.setFixedPrice(BigDecimal.valueOf(24999.10));
        product.setStorage(2000);
        productMapper.insert(product);
    }

    @Test
    void pro(){
        List<Product> productList = productMapper.getProductByName("测试");
        productList.forEach(System.out::println);
    }

    @Test
    void print(){
        Map<String,List<String>> mapList= new HashMap<>();
        int i = 1;
            for (Map.Entry<String,List<String>> entry: mapList.entrySet()
                 ) {
                System.out.println("["+i+"]key:"+entry.getKey()+" Value:"+entry.getValue());
                i++;
            }
    }

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ProductServicesImpl productServices;
    @Autowired
    CategoryServicesImpl categoryServices;
    @Test
    void test(){
        List<String> stringListMap = categoryServices.queryAllFatherWithNoChildren();
        System.out.println(stringListMap);
    }

    @Autowired
    CollectionMapper collectionMapper;

    @Test
    void collect(){
        List<Collection> list = collectionMapper.queryUserCollection("1");
        list.forEach(System.out::println);
    }

    @Test
    void comments(){
        
    }

    @Test
    void encry(){
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        System.out.println(Arrays.toString(key));
    }

    @Autowired
    StatCode statCode;

    private static String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCXk2pKmH494fLHytx0VNbx3dL/Icqnyo1lfke8J7btW+hHPfKMtBzCm0DyHiEUGFUsDFBbO3VnxNO/PMy/BPzGlfu/KdZqnHg3yIKGDw+u1cH2yLklKuTa2lHgLvJFKLS35l5YuKKHSVrTzKQjdSDXFPbj8GCToGVj19FRGQHP98BkoLrs8XGARdpoK4MNJEIDJ2S/ch/blk54X4ZJASYJonAiLoLmpKOQLKiDdnZT7JhN0s8RoAp2HhgVx82QKXVI66yWv7+BlJBj4cVMB2Fx/YNcpBf4C5PjIwVua5f1BbuVDks98NguydqKJCZ9FYm7SK91mk3wBC3EpVJRsbJFAgMBAAECggEACO6g9uBhoBfujKOBtkFBoWMnCe3d2WFwT6jPZWEZwIcH+xrFblE22Kj5fMsVu+9rJXtcSOQKGSFaMQzOK/uJQdKY0T0P65xo4TCpsGi2zeC+22Ictvn+Hn97D7D0ierKK4QhzyyiN0K1XYHTsls2ojYUDn6d0lVDZEzVgufsWWry2tIetf/yG8/DD307VtBFyDBwzKoG3GQHIJTSYDS/kB1XC+Hw298bKfx+5xxYs5CHhlFMDaV2m1yftrySE+oYxSvQZ27eA6XSty7TQLNd3F17VU7Jjndqkc85iG7lzbSXsMybzDNqMCaWcAof5JsxiMExBGY4zwR4UNI5rillyQKBgQDVe0RB2yq54RYx2q1xfG17raleqlS7jci2enohuxHqTe7Cx9n9sbbwNIxXPfbh/yjKzlOUsbQ9QXH7Rtnr73WpqKiXysRCo0Pk8jNt0wOuIVN1a0gkPyjP6BlkDgdTf4S+KlITE19LAiT8OaXrueALTnbdVYMM8yaKnvVB6Wf7wwKBgQC1w8c50uSKCFNlQT4+5wuVHlOqzGjbl7hp8sNj9Ly873p1/RC52dpGtSLX/hdBnGfbXLVLJialxtHTI8HyCOTNZeQN0f8amNtzTHVOW0bgzkFXVht2wSxobGbfk4lfVN1NIlSB0tteiYbeI+k4l8kKB1pBHeM0cZQaYMehBQ0hVwKBgDU06cg4Jaf6T5vRUtUsG+VtXP85GLE+Hz5LvFDX3Rk2n0FwqgYu2td3YVsFGJkvDS2uyuoOeEukGhJ0kC/bmMnWPV7HrK6nHm7bZrICm7I0oCZtvKc8DqHyyRFE5dKLv7vdtGMIuwFo4Wrzu0TLKOnofnufqm/50itOfvYUhFQdAoGAVTeywhNz/pskrkZGbqtYLNOJGny3dKzPUyz0Gnr1ugb0/ThmjRvsUFZAPc/tC1/O+XOfs9nxKKvsV/ddyKIO05W/tBMObWPZD1d7b/8vXdqVm+LNAYJT1altGclMEJvzHWTJBgzbuJj12FQx1cyj/sWvtPPUQBQ4AZ36Oi3qGAUCgYBOANYzjrvV5UPmwszgSOwqA3hjfQW0rHpxkwR98H7RHyKrodizKRzpR8EVkGvZ6iDJRyzVbwutwS139jvslwp2c0ucB5OTeAH+XV7EQkUXqMLBGpda0PdJdLeqWpERMehufTSE9+JwYrE7tDvzcJ+4TpxhVDj0BwLjRP5lTtM5Vg==";

    private static String aliyPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh6/Q7TYocx6nXWbhggK7pYQoeSMd/ptiDNm42TzgMAFesJh5he4Ome9CDG4HPfTVRgGt7m5zgrFkwRHv4uRT6E8q4E++1I/Gk/riS6eyRymhu/b3Vq/vMONc/bO1hMEpc+34XOPrYA3lyhj5802EuqwCxFcKY4o2dcyTUXo08bOtGV51ANAnXzcE9P/vMA5OuOYAVNQx+eSp24ry4szg0DKc3EUTXsHEp33ZILdK/yVV7NZ+Tfhar7iyNcsU5uF3AAX3A1DOgqgIIZQ3l0+xPBcvHIXkzh0m+R7M5OZ0/E9xJMej+VZKUB2sDJbuVOm4a8VPXhWz2eq1Ni2hwL84eQIDAQAB";

    @Test
    void AilyPay(){
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do","2021000120616309",privateKey,"json","UTF-8",aliyPublicKey,"RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl("http://43.138.237.251/getjson");
        request.setReturnUrl("http://localhost:8080/order/ok");
        JSONObject bizContent = new JSONObject();

        //List<Order> orders = object.getList("orders",Order.class);
        BigDecimal amount = new BigDecimal(0);
//        for (Order order:orders) {
//            amount=amount.add(order.getTotalPrice());
//        }
        //String payfor = "订单号"+object.getString("boundId");
        bizContent.put("out_trade_no",NanoIdUtils.randomNanoId());//object.getString("boundId")
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "test");
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
        String response = "";
        try{
            response = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }


    @Test
    void comment(){
        Comment comment = new Comment();
        comment.setCommentId(NanoIdUtils.randomNanoId());
        comment.setUserId("2");
        comment.setProductId("1582292084576550913");
        comment.setContext("comtext");
        comment.setCreateTime(new Timestamp(new Date().getTime()));
        commentMapper.insert(comment);
    }

}
