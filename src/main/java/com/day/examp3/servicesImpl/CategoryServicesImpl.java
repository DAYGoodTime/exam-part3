package com.day.examp3.servicesImpl;

import com.alibaba.fastjson2.JSONObject;
import com.day.examp3.mapper.CategoryMapper;
import com.day.examp3.services.CategoryServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoryServicesImpl  implements CategoryServices {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<String> queryKeyWordsByCategory(String category) {
        List<Map<String, String>> mapList = categoryMapper.queryChildrenByFather(category);
        List<String> keywords = new ArrayList<>();
        for (Map<String,String> map:mapList) {
            keywords.add(map.get("children_category"));
        }
        return keywords;
    }

    @Override
    public List<Map<String, String>> queryAllFatherCategory() {
        List<String> names = new ArrayList<>();
        List<Map<String, String>> hasChildrenMap = categoryMapper.queryAllFatherCategoryWithChildren();
        List<Map<String, String>> noChildrenMap = categoryMapper.queryAllFatherCategoryWithNoChildren();
        hasChildrenMap.addAll(noChildrenMap);
        return hasChildrenMap;
    }

    @Override
    public List<JSONObject> queryAllFatherWithChildren() {
        List<JSONObject> jsonList = new ArrayList<>();
        List<Map<String,String>> hasChildrenMap = categoryMapper.queryAllFatherCategoryWithChildren();
        for (Map<String,String> map :hasChildrenMap) {
            JSONObject json = new JSONObject();
            String fatherId = map.get("parent_category");
            json.put("parent_id",fatherId);
            json.put("parent_name",map.get("parent_name"));
            json.put("children",categoryMapper.queryChildrenByFather(fatherId));
            jsonList.add(json);
        }
        return jsonList;
    }

    @Override
    public List<String> queryAllFatherWithNoChildren() {
        List<Map<String, String>> maps = categoryMapper.queryAllFatherCategoryWithNoChildren();
        List<String> fathers = new ArrayList<>();
        for (Map<String,String> map:maps) {
            fathers.add(map.get("parent_name"));
        }
        return fathers;
    }


}
