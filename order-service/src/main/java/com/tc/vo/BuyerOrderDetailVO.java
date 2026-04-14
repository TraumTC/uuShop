package com.tc.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyerOrderDetailVO {
    private String orderId;
    private String detailId;
    private Integer productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer productQuantity;
    private String productIcon;
}
