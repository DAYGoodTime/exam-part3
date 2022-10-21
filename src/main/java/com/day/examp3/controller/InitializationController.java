package com.day.examp3.controller;

import com.day.examp3.mapper.ProductMapper;
import com.day.examp3.mapper.UserMapper;
import com.day.examp3.pojo.Product;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
/**
 * 用于初始化各个页面需要初始化参数的页面
 */
public class InitializationController {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserMapper userMapper;

    /**
     * 首页初始化
     * @param model
     * @return
     */
    @RequestMapping("/main")
    public String main(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","FLOWERS");
        List<Product> products_flower = productMapper.selectByMap(map);
        model.addAttribute("products_flower",products_flower);
        map.replace("keywords","NORDIC_WALL");
        List<Product> products_nordic = productMapper.selectByMap(map);
        model.addAttribute("products_nordic",products_nordic);
        map.replace("keywords","UNIQUE_TRUE_LOVE");
        List<Product> products_love = productMapper.selectByMap(map);
        model.addAttribute("products_love",products_love);
        return "index";
    }

    /**
     * Paint页面初始化
     * @param model
     * @return
     */
    @RequestMapping("/paint")
    public String toPaint(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","SIMPLE_MODERN");
        List<Product> products_modern = productMapper.selectByMap(map);
        model.addAttribute("products_modern",products_modern);

        map.replace("keywords","EUROPEAN");
        List<Product> products_european = productMapper.selectByMap(map);
        model.addAttribute("products_european",products_european);

        map.replace("keywords","AMERICAN");
        List<Product> products_american = productMapper.selectByMap(map);
        model.addAttribute("products_american",products_american);
        map.replace("keywords","AMERICAN_2");
        List<Product> products_american2 = productMapper.selectByMap(map);
        model.addAttribute("products_american2",products_american2);
        return "paint";
    }

    @RequestMapping("/flowerDer")
    public String toFlowerDer(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","proList");
        List<Product> proList = productMapper.selectByMap(map);
        model.addAttribute("proList",proList);
        map.replace("keywords","vase");
        List<Product> vases = productMapper.selectByMap(map);
        model.addAttribute("vases",vases);
        return "flowerDer";
    }
    @RequestMapping("/flowerDer/proList")
    public String toProList(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","proList");
        List<Product> proList = productMapper.selectByMap(map);
        model.addAttribute("proList",proList);
        return "proList";
    }
    @RequestMapping("/flowerDer/vaseProList")
    public String toVase_proList(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","vase");
        List<Product> vases = productMapper.selectByMap(map);
        model.addAttribute("vases",vases);
        return "vase_proList";
    }

    @RequestMapping("/decoration")
    public String toDecoration(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","decoration");
        List<Product> decorations = productMapper.selectByMap(map);
        model.addAttribute("decorations",decorations);
        return "decoration";
    }
    @RequestMapping("/decoration/zbproList")
    public String toZbproList(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","decoration");
        List<Product> decorations = productMapper.selectByMap(map);
        model.addAttribute("decorations",decorations);
        return "zbproList";
    }


    @RequestMapping("/perfume")
    public String toPerfume(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","AROMATHERAPY");
        List<Product> aromatherapys = productMapper.selectByMap(map);
        model.addAttribute("aromatherapys",aromatherapys);
        map.replace("keywords","BURNER");
        List<Product> burners = productMapper.selectByMap(map);
        model.addAttribute("burners",burners);
        return "perfume";
    }



    @RequestMapping("/idea")
    public String toIdea(Model model){
        Map<String,Object> map = new HashMap<>();
        map.put("keywords","fashion");
        List<Product> fashions = productMapper.selectByMap(map);
        model.addAttribute("fashions",fashions);
        map.replace("keywords","novel");
        List<Product> novels = productMapper.selectByMap(map);
        model.addAttribute("novels",novels);
        map.replace("keywords","life");
        List<Product> lifes = productMapper.selectByMap(map);
        model.addAttribute("lifes",lifes);
        return "idea";
    }

    @RequestMapping("/user/cartList")
    public String toCart(HttpSession session, Model model){
        String user_id = (String) session.getAttribute("user_id");
        List<Map<String, Object>> userShoppingList = userMapper.getUserShoppingList(user_id);
        model.addAttribute("shoppingListMap",userShoppingList);
        return "cart";
    }


}
