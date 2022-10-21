package com.day.examp3.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("lmonkey_deliver")
public class Deliver {
    @TableId("deliver_id")
    private Integer deliverId;
    private String deliverName;
}
