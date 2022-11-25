package com.day.examp3.utils;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于获取各种系统状态的工具类
 */
public class Monitor {
    public static Map<String, String> getDisk(){
        Map<String,String> map = new HashMap<>();
        File[] files =File.listRoots();
        BigDecimal Total = new BigDecimal(0);
        BigDecimal Free = new BigDecimal(0);
        for (File file :files) {
            BigDecimal total =new BigDecimal(new DecimalFormat("#.#").format(file.getTotalSpace() *1.0 /1024 /1024 /1024));
            BigDecimal free =new BigDecimal(new DecimalFormat("#.#").format(file.getFreeSpace() *1.0 /1024 /1024 /1024));
            BigDecimal un =new BigDecimal(new DecimalFormat("#.#").format(file.getUsableSpace() *1.0 /1024 /1024 /1024));
            Total = Total.add(total);
            Free = Free.add(free);
        }
        map.put("Total",Total+"G");
        map.put("Free",Free+"G");
        map.put("Used",Total.subtract(Free)+"G");
        return map;
    }
    public static Map<String, String> getMem(){
        Map<String,String> map = new HashMap<>();
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryMXBean =ManagementFactory.getMemoryMXBean();
        // 总的物理内存
        BigDecimal totalMemorySize =new BigDecimal(new DecimalFormat("#.##").format(osmxb.getTotalPhysicalMemorySize() /1024.0 /1024 /1024));
        // 已使用的物理内存
        BigDecimal usedMemory =new BigDecimal(new DecimalFormat("#.##").format((osmxb.getTotalPhysicalMemorySize() -osmxb.getFreePhysicalMemorySize()) /1024.0 /1024 /1024));
        map.put("Total",totalMemorySize.toString()+"G");
        map.put("Used",usedMemory.toString()+"G");
        return map;
    }
    public static Map<String, String> getCpuInfo() {
        Map<String,String> map = new HashMap<>();
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        map.put("CPU_core",String.valueOf(cpuInfo.getCpuNum()));
        map.put("CPU_used",cpuInfo.getUsed() +"%");
        return map;
    }

}
