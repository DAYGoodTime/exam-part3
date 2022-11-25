package com.day.examp3.controller;

import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.CommentMapper;
import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.pojo.Comment;
import com.day.examp3.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
public class ProductController {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CommentMapper commentMapper;

    @RequestMapping("/proDetail/{pid}")
    public String toProDetail(@PathVariable("pid")String product_id,Model model){
        Product product = productMapper.selectById(product_id);

        if(product==null){
            //TODO 查询产品失败
        }
        //加载该产品信息
        model.addAttribute("product",product);
        //加载评论信息
        List<Comment> comments = commentMapper.getCommentsByProductId(product_id);
        model.addAttribute("comments",comments);
        return "proDetail";
    }

    @RequestMapping(value = "/getProDetail/{pid}",method = RequestMethod.POST)
    @ResponseBody
    public String getProDetail(@PathVariable("pid")String product_id){
        Product product = productMapper.selectById(product_id);
        if(product==null){
            //TODO 查询产品失败
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pro",product);
        return jsonObject.toJSONString();
    }

}
