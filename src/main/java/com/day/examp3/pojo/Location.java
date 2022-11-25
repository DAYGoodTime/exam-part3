package com.day.examp3.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("china")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Location {

    private Integer id;

    private String name;

    private Integer pid;
}
