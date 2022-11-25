package com.example.demo.utils;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class Cos {

    public static final String COS_URL = "https://daycos-1259214930.cos.ap-guangzhou.myqcloud.com/imgs/";

    public static final String BUKKIT_NAME = "daycos-1259214930";

    static {
// 1 初始化用户身份信息（secretId, secretKey）。
// SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        String secretId = "AKIDHNCk35qKYU9XeV5OQChvsZz8zZtwiCPL";
        String secretKey = "g7ICpBG5ctsb3E7k66pHTO1NSGn32j9I";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
// 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-guangzhou");
        ClientConfig clientConfig = new ClientConfig(region);
// 这里建议设置使用 https 协议
// 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
// 3 生成 cos 客户端。
        cosClient = new COSClient(cred, clientConfig);
    }
    public static COSClient cosClient;

    /**
     * 上传文件到cos/imgs/fileName下
     * @return PutObjectResult类
     */
    public static PutObjectResult upLoadImgs(MultipartFile file){
        String fileName = file.getOriginalFilename();
        if(fileName==null){
            return null;
        }
        File temp = null;
        try {
            temp = File.createTempFile(fileName,null);
            temp = FileUtil.writeFromStream(file.getInputStream(),temp);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(temp==null){
            return null;
        }
        // 指定文件将要存放的存储桶
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        String key = "imgs/"+fileName;
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUKKIT_NAME,key,temp);
        return cosClient.putObject(putObjectRequest);
    }

}
