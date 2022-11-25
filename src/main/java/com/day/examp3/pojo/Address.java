package com.day.examp3.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("lmonkey_receive_address")
public class Address {

  @TableId(value = "receive_address_id",type = IdType.AUTO)
  private Long receiveAddressId;
  private String userId;
  private String receiveName;
  private String province;
  private String address;
  private String zipcode;
  private String mobile;
  private String telephone;
  private String email;
  private String area;
  private Integer isDefault;

  private Integer isDeleted;

}
