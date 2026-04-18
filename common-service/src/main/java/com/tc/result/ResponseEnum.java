package com.tc.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseEnum {
    PRODUCT_NULL(301,"商品信息不存在"),
    PRODUCT_STOCK_EMPTY(302,"商品库存不足"),
    PRODUCT_STOCK_NULL(303,"商品已售罄"),
    PRODUCT_SUBSTOCK_ERROR(304,"商品减库存失败"),
    ORDER_CREATE_ERROR(305,"订单创建失败"),
    ORDER_NULL(306,"订单不存在"),
    ORDER_CANCEL_ERROR(307,"订单取消失败"),
    ORDER_CANCEL_FINISHED(307,"已完成订单无法取消"),
    ORDER_FINISHED(307,"已完成订单无法再次完成"),
    ORDER_FINISH_CANCELED(309,"已取消订单无法完成"),
    ORDER_CANCELED(308,"已取消订单无法再次取消"),
    ORDER_STATUS_ERROR(308,"订单状态异常"),
    ORDER_FINISH_ERROR(309,"订单完成失败"),
    ORDER_CANCEL_PAY_ERROR(309,"已取消的订单无法支付"),
    ORDER_FINISH_PAY_ERROR(309,"已完成的订单无法支付"),
    ORDER_PAY_ERROR(310,"订单已支付"),
    ORDER_PAY_FAIL(311,"订单支付失败"),
    MOBILE_ERROR(312,"手机格式错误"),
    MOBILE_EXIST(313,"手机号已存在"),
    CODE_ERROR(314,"验证码错误"),
    USER_REGISTER_ERROR(315,"用户注册失败"),
    USER_NOT_EXIST(316,"用户不存在"),
    PASSWORD_ERROR(317,"密码错误"),
    TOKEN_ERROR(318,"登录信息失效"),
    MOBILE_NULL(319,"手机号码为空"),
    SMS_SEND_ERROR(320,"验证码发送失败"),
    PRODUCT_ADD_ERROR(321,"商品添加失败"),
    ID_NULL(322,"ID为空"),
    PRODUCT_NOT_EXIST(323,"商品不存在"),
    PRODUCT_DELETE_ERROR(324,"商品删除失败"),
    PRODUCT_STATUS_NULL(325,"商品状态为空"),
    PRODUCT_STATUS_TRUE_ERROR(325,"商品已上架，请勿重复上架"),
    PRODUCT_STATUS_FALSE_ERROR(325,"商品已下架，请勿重复下架"),
    PRODUCT_STATUS_UPDATE_ERROR(326,"商品状态修改失败"),
    PRODUCT_UPDATE_ERROR(327,"商品修改失败"),
    ORDER_NOT_EXIST(328,"订单不存在"),
    ORDER_CANCEL_FAIL(329,"订单取消失败"),
    ORDER_FINISH_FAIL(330,"订单完成失败"),
    ADMIN_USERNAME_NOT_EXIST(331,"管理员用户名不存在"),
    ADMIN_PASSWORD_ERROR(332,"管理员密码错误");;


    private Integer code;
    private String msg;
}
