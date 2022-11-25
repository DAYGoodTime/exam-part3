package com.day.examp3.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 用于生成随机字符的工具类
 */
@Component
public class NanoIdUtils {
    public static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();
    // 随机生成的字符内容
    public static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    // 随机生成 ID 的位数
    public static final int DEFAULT_SIZE = 10;

    private NanoIdUtils() {
    }

    public static String randomNanoId() {
        return randomNanoId(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, DEFAULT_SIZE);
    }

    public static String randomNanoId(Random random, char[] alphabet, int size) {
        if (random == null) {
            return randomNanoId(DEFAULT_NUMBER_GENERATOR,alphabet,size);
        } else if (alphabet == null) {
            return randomNanoId(random,DEFAULT_ALPHABET,size);
        } else if (alphabet.length != 0 && alphabet.length < 256) {
            if (size <= 0 ) {
                return randomNanoId(random,alphabet,DEFAULT_SIZE);
            } else {
                int mask = (2 << (int)Math.floor(Math.log((double)(alphabet.length - 1)) / Math.log(2.0D))) - 1;
                int step = (int)Math.ceil(1.6D * (double)mask * (double)size / (double)alphabet.length);
                StringBuilder idBuilder = new StringBuilder();

                while(true) {
                    byte[] bytes = new byte[step];
                    random.nextBytes(bytes);

                    for(int i = 0; i < step; ++i) {
                        int alphabetIndex = bytes[i] & mask;
                        if (alphabetIndex < alphabet.length) {
                            idBuilder.append(alphabet[alphabetIndex]);
                            if (idBuilder.length() == size) {
                                return idBuilder.toString();
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("alphabet must contain between 1 and 255 symbols.");
        }
    }
}
