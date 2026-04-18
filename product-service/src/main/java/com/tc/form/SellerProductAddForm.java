package com.tc.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellerProductAddForm {
    private Integer categoryType;
    private String productDescription;
    private String productIcon;
    private String productName;
    private BigDecimal productPrice;
    private Integer productStatus;
    private Integer productStock;

}
