package com.day.examp3.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("lmonkey_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @TableId(value = "category_id",type = IdType.AUTO)
    private Integer categoryId;
    private String parentCategory;
    private String parentName;
    @TableField(value = "children_category",fill = FieldFill.INSERT_UPDATE)
    private String childrenCategory;
    @TableField(value = "children_name",fill = FieldFill.INSERT_UPDATE)
    private String childrenName;

}
