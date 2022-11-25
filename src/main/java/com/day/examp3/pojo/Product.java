package com.day.examp3.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@TableName("lmonkey_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  @TableId(value ="product_id" ,type = IdType.ASSIGN_ID)
  private String productId;

  private String name;
  private String keywords;
  private Date addTime;
  private String picture;
  private String bigPicture;
  private BigDecimal fixedPrice;
  private BigDecimal lowerPrice;
  private String description;
  private String catalog;
  private String extracts;
  private Integer storage;
}
