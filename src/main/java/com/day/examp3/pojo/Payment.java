package com.day.examp3.pojo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Mapper;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("lmonkey_payment")
public class Payment {
    @TableId("payment_id")
    private Integer paymentId;
    private String payment;
}
