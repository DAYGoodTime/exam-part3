package com.day.examp3.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@TableName(value = "lmonkey_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @TableId(value = "user_id",type = IdType.ASSIGN_ID)
  private String userId;
  private String loginName;
  private String nickName;
  private String realName;
  private Long gradeId;
  private String password;
  private String email;
  private String province;
  private String recommender;
  private String sex;
  private Timestamp birth;
  private String location;
  private String school;
  private String company;
  private String cardNumber;
  private String mobile;
  private String phone;
  private String msn;
  private String qq;
  private String website;
  private String sendAddress;
  private String zipcode;
  private String hobby;
  private String verifyFlag;
  private String verifyCode;
  private Timestamp lastLoginTime;
  private String lastLoginIp;
  private String area;
  private String headPic;

  public Date getBirthDate(){
//    if(this.birth.getTime()==null) return new Date(0);
    return new Date(this.birth.getTime());
  }

}
