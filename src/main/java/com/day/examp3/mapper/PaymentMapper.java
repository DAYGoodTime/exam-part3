package com.day.examp3.mapper;

import com.day.examp3.pojo.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentMapper {

    /**
     * 获得该支付id的支付名字
     * @param payId 支付ID
     * @return 支付名字
     */
    @Select("select payment from lmonkey_payment where payment_Id = #{payId}")
    String getPaymentName(@Param("payId")Integer payId);

    /**
     * 查询所有支付对象
     * @return 所有的支付对象
     */
    @Select("select * from lmonkey_payment")
    List<Payment> queryAllPayment();

    /**
     * 根据支付ID 查询对应的支付对象
     * @param payId 支付ID
     * @return 支付对象
     */
    @Select("select  * from lmonkey_payment where payment_id = #{payId}")
    Payment getPayment(@Param("payId")Integer payId);
}
