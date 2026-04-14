package com.tc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author tc
 * @since 2026-04-13
 */
@Data
@TableName("order_master")
public class OrderMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("order_id")
    private String orderId;

    /**
     * 买家名字
     */
    @TableField("buyer_name")
    private String buyerName;

    /**
     * 买家电话
     */
    @TableField("buyer_phone")
    private String buyerPhone;

    /**
     * 买家地址
     */
    @TableField("buyer_address")
    private String buyerAddress;

    /**
     * 买家id
     */
    @TableField("buyer_openid")
    private Integer buyerOpenid;

    /**
     * 订单总金额
     */
    @TableField("order_amount")
    private BigDecimal orderAmount;

    /**
     * 订单状态，默认0新下单,1完成，2取消
     */
    @TableField("order_status")
    private Integer orderStatus;

    /**
     * 支付状态，默认0未支付，1已支付
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
