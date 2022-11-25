package com.day.examp3.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.springframework.stereotype.Component;

/**
 * 根据Hutool工具类当中的[对称加密]方法进行二次²封装
 * 图一乐
 */
@Component
public class AESUtil {
    private static final AES aes;

    //是否每次重启的时候使用随机生成的key
    private static boolean isRestKeyOnReload=false;

    private static final byte[] key ={-55, -127, 89, 85, -58, 33, 14, -60, 24, 60, -38, 82, 113, 122, -27, 8};

    static {
        // 随机生成密钥
        byte[] key2 = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        // 构建
        if(isRestKeyOnReload){
            aes = SecureUtil.aes(key);
        }else {
            aes = SecureUtil.aes(key2);
        }

    }
    public String encryptHex(String content){
        return aes.encryptHex(content);
    }

    public void setIsRestKeyOnReload(boolean flag){
        isRestKeyOnReload = flag;
    }

    public String decryptStrHex(String encryptHex){
        return aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
    }
}
