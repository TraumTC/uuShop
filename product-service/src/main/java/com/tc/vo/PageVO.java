package com.tc.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO {
    private List<SellerProductInfoVO> content;
    private Long size;
    private Long total;
}