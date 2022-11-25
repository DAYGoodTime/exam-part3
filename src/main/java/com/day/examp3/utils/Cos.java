package com.day.examp3.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.idrsolutions.image.png.PngCompressor;
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
import java.io.IOException;

@Component
/**
 * 腾讯云Cos的操作工具类
 * 记得填自己的秘钥
 */
public class Cos {

    public static final String COS_URL = "https://YOURCOS_URL";

    public static final String BUKKIT_NAME = "YOURCOSBUKKITNAME";

    static {
        // 1 初始化用户身份信息（secretId, secretKey）。
        // SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        String secretId = "SECRETID";
        String secretKey = "SECRETKEY";
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
     * 封装cosClient上传图片到对象存储当中
     * @param file 表单文件
     * @param fileName 指定文件名字
     * @return 返回URL编码后的访问地址
     */
    public static String upLoadImgs(MultipartFile file,String fileName){
        if(fileName==null){
            return null;
        }
        String objName = fileName.substring(0,fileName.length() - 4);
        File tempImg = null;
        File compressedFile = null;
        try {
            tempImg = File.createTempFile(objName,".png");
            compressedFile = File.createTempFile(objName,".png");
            tempImg = FileUtil.writeFromStream(file.getInputStream(),tempImg);
            PngCompressor.compress(tempImg,compressedFile);
        }catch (IOException e){
            e.printStackTrace();
        }
        if(compressedFile==null){
            return null;
        }
        // 指定文件将要存放的存储桶
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        String key = "imgs/"+compressedFile.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUKKIT_NAME,key,compressedFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        if (putObjectResult==null) return null;
        String url = COS_URL + "/" + key;
        return URLUtil.encode(url);
    }

}
