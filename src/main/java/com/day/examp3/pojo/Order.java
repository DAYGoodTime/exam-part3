package com.day.examp3.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  @TableId(value = "order_id")
  private String orderId;
  private String userId;
  private User user;
  private String status;
  private java.sql.Timestamp orderTime;
  private Long totalPrice;

  private String payment;

  private Long invoiceId;
  private Long receiveAddressId;
  private Address receiveAddress;
  private String bak;

  private String productId;
  private List<Product> productList;
  private Integer amount;



  public Date getDateTime(){
    return new Date(this.orderTime.getTime());
  }

}
