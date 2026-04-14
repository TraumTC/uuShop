package com.tc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单详情表
 * </p>
 *
 * @author tc
 * @since 2026-04-13
 */
@Data
@TableName("order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("detail_id")
    private String detailId;

    @TableField("order_id")
    private String orderId;

    @TableField("product_id")
    private Integer productId;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品单价
     */
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 商品数量
     */
    @TableField("product_quantity")
    private Integer productQuantity;

    /**
     * 商品小图
     */
    @TableField("product_icon")
    private String productIcon;

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
