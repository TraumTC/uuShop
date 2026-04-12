package com.tc.controller;

import com.tc.entity.ProductInfo;
import com.tc.service.ProductCategoryService;
import com.tc.service.ProductInfoService;
import com.tc.vo.CategoryVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 类目表 前端控制器
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {
    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private ProductInfoService productInfoService;
    @GetMapping("/list")
    public List<CategoryVo> list() {
        List<CategoryVo> categoryVoList = productCategoryService.categoryList();
        return categoryVoList;
    }

    @GetMapping("/findPriceById/{id}")
    public BigDecimal findPriceById(@PathVariable("id") Integer id) {
        ProductInfo productInfo = productInfoService.getById(id);
       return productInfo.getProductPrice();
    }
}
