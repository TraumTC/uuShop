package com.tc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@FeignClient("product-service")
public interface ProductFeign {
//    查询单价
    @GetMapping("/buyer/product/findPriceById/{id}")
   public BigDecimal findPriceById(@PathVariable("id") Integer id);
//    修改商品数量
    @PutMapping("/buyer/product/subStockById/{id}/{quantity}")
    public Boolean subStockById(@PathVariable("id") Integer id,@PathVariable("quantity") Integer quantity);
}

