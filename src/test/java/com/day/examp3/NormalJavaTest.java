package com.day.examp3;

import com.day.examp3.config.OwnConfig;
import com.google.common.collect.Maps;
import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NormalJavaTest {

    @Test


    public void test() throws InterruptedException {
        int[][] b={{1}, {2,2}, {2,2,2}};

        int sum=0;

        for(int i=0;i<b.length;i++) {

            for(int j=0;j<b[i].length;j++) {

                sum+=b[i][j];

            }

        }

        System.out.println(sum);
    }

    @Test
    public void formatByte() throws InterruptedException {

        }
}
