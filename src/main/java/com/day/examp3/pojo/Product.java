package com.day.examp3.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@TableName("lmonkey_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  @TableId(value ="product_id" ,type = IdType.ASSIGN_ID)
  private String productId;

  private String name;
  private String keywords;
  private java.sql.Date addTime;
  private String picture;
  private String bigPicture;
  private Double fixedPrice;
  private Double lowerPrice;
  private String description;
  private String author;
  private String publishing;
  private java.sql.Timestamp publishTime;
  private String isbn;
  private String language;
  private String whichEdition;
  private String totalPage;
  private String bindLayout;
  private String bookSize;
  private String editorDescription;
  private String catalog;
  private String bookSummary;
  private String authorSummary;
  private String extracts;
  private java.sql.Date printTime;
  private Long printNumber;
  private String paperType;
  private String printFrequency;
  private Integer storage;
  private Integer o_amount; //作为订单时候的数量

}
