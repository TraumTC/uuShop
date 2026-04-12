package com.tc.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.swing.*;
import java.math.BigDecimal;
@Data
public class InfoVo {
    @JsonProperty("id")
    private Integer productId;
    @JsonProperty("name")
    private String productName;
    @JsonProperty("price")
    private BigDecimal productPrice;
    @JsonProperty("description")
    private String productDescription;
    @JsonProperty("icon")
    private String productIcon;
    private Integer quantity;
    @JsonProperty("stock")
    private Integer productStock;

}
