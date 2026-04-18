package com.tc.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellerProductUpdateForm {
    private Boolean status;
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String icon;
    private SellerProductUpdateInnerForm category;
}
