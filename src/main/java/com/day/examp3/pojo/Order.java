package com.day.examp3.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("lmonkey_order")
public class Order {

  @TableId(value = "order_id")
  private String orderId;


  private String userId;
  private User user;
  private String status;
  private java.sql.Timestamp orderTime;
  private BigDecimal totalPrice;

  private Integer paymentId;
  private Payment payment;

  private Long invoiceId;
  private Long receiveAddressId;
  private Address receiveAddress;
  private String bak;

  private String productId;
  private Product product;

  private Integer amount;
  private String commentId;

  private String BoundId;


  public Date getDateTime(){
    return new Date(this.orderTime.getTime());
  }

}
