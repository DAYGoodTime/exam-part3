package com.day.examp3.mapper;

import com.day.examp3.pojo.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentMapper {

    //TODO 注释
    @Select("select payment from lmonkey_payment where payment_Id = #{payId}")
    String getPaymentName(@Param("payId")Long payId);

    @Select("select * from lmonkey_payment")
    List<Payment> queryAllPayment();
}
