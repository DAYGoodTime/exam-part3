package com.day.examp3;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class NormalJavaTest {

    @Test
    public void test(){
        List<Object> objectList = new ArrayList<>();
        objectList.add(null);
        objectList.forEach(System.out::println);
    }

}
