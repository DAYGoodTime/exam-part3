package com.day.examp3.controller;

import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProductController {

    @Autowired
    ProductMapper productMapper;

    @RequestMapping("/proDetail/{pid}") //TODO 产品链接
    public String toProDetail(@PathVariable("pid")String product_id, Model model){
        Product product = productMapper.selectById(product_id);

        if(product==null){
            //TODO 查询产品失败
        }
        //加载该产品信息
        model.addAttribute("product",product);
        return "proDetail";
    }

}
