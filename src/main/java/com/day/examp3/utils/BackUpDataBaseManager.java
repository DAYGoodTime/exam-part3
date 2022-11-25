package com.day.examp3.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 备份数据库的工具类
 */
@Component
public class BackUpDataBaseManager {

    @Value("${spring.datasource.username}")
    private String UserName;

    @Value("${spring.datasource.password}")
    private String DsPwd;

    @Value("${datasource.name}")
    private String DBName;

    @Autowired
    DataSource dataSource;


    private static final String DBPort = "3306";
    private static final String Host = "localhost";

    public static final String sqlPath = "D:\\sqlBackup\\";

    public static final String sqlTempPath = "D:\\sqlBackup\\temp\\";

    public boolean backUpDB(){
        String fileName = DataUtil.getCurrentTimeAsString() + "-" + DBName +".gz";
        StringBuilder sb = new StringBuilder();
        // 拼接备份命令
        sb.append("mysqldump ").append("--column-statistics=0").append(" -u").append(UserName).append(" -p").append(DsPwd).append(" -h").append(Host);
        sb.append(" -P").append(DBPort).append(" --databases ").append(DBName).append(" > ").append(sqlPath).append(fileName.replace(".gz",".sql"));
        try {
            existsFile(new File(sqlPath+fileName));
            Process exec = Runtime.getRuntime().exec(sb.toString());
            byte[] zipData = ZipUtil.gzip(exec.getInputStream());
            Files.write(Paths.get(sqlPath+fileName),zipData);
            if (!exec.isAlive()){
                System.out.println("备份数据库成功:"+sqlPath);
                return true;
            }
        } catch (IOException e) {
            System.out.println("IO异常");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从备份目录里面寻找指定的备份名,解压并执行sql脚本
     * @param fileName 被压缩的sql脚本文件名
     * @return 是否恢复成功
     */
    public boolean recoveryDB(String fileName){
        if(!new File(sqlPath+fileName).exists()) return false;
        String sqlName = fileName.replace(".gz",".sql");
        existsFile(new File(sqlTempPath+sqlName));

        StringBuilder sb2 = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        sb2.append("cmd /c mysql ").append("-u").append(UserName).append(" -p").append(DsPwd).append(" ").append(DBName).append(" < ").append(sqlTempPath).append(sqlName);
        System.out.println(sb2);
        try {
            byte[] unGzip = ZipUtil.unGzip(Files.readAllBytes(Paths.get(sqlPath + fileName)));
            Files.write(Paths.get(sqlTempPath+sqlName),unGzip);
            if(waitForDone(new File(sqlTempPath+sqlName), (long) unGzip.length)){
                //mysql -h localhost -u root -p
                Process exec = Runtime.getRuntime().exec(sb2.toString());
                if(exec.waitFor()==0){
                    FileUtil.del(new File(sqlTempPath+sqlName));//执行完脚本后删除脚本
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 判断文件是否存在，不存在创建
     */
    private static void existsFile(File file) {
        // 判断文件路径是否存在,不存在新建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean waitForDone(File file,Long size){
        if(file.length()==size){
            return true;
        }
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitForDone(file,size);
        return false;
    }
}
