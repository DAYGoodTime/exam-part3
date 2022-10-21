package com.day.examp3.utils;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DataUtil {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Timestamp StringToStamp(String data){
        try {
            return new Timestamp(sdf.parse(data).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DataToString(Date date){
        return sdf.format(date);
    }

}
