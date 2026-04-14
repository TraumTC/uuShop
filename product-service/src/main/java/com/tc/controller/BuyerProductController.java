package com.tc.controller;

import com.tc.entity.ProductInfo;
import com.tc.excepion.ShopException;
import com.tc.result.ResponseEnum;
import com.tc.service.ProductCategoryService;
import com.tc.service.ProductInfoService;
import com.tc.util.ResultVOUtil;
import com.tc.vo.ResultVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
    public ResultVO list() {

        return ResultVOUtil.success(this.productCategoryService.categoryList());
    }

    @GetMapping("/findPriceById/{id}")
    public BigDecimal findPriceById(@PathVariable("id") Integer id) {
        ProductInfo productInfo = this.productInfoService.getById(id);
        if(productInfo == null) throw new ShopException(ResponseEnum.PRODUCT_NULL.getMsg());
       return productInfo.getProductPrice();
    }

    @GetMapping("/findById/{id}")
    public ProductInfo findById(@PathVariable("id") Integer id) {
        ProductInfo productInfo = this.productInfoService.getById(id);
        if(productInfo == null) throw new ShopException(ResponseEnum.PRODUCT_NULL.getMsg());
        return productInfo;
    }
    @PutMapping("/subStockById/{id}/{quantity}")
    public Boolean subStockById(@PathVariable("id") Integer id,@PathVariable("quantity") Integer quantity) {
        ProductInfo productInfo = this.productInfoService.getById(id);
//        商品不存在
        if(productInfo == null) throw new ShopException(ResponseEnum.PRODUCT_NULL.getMsg());
        Integer stock = productInfo.getProductStock();
//        商品已售罄
        if(stock == 0) throw new ShopException(ResponseEnum.PRODUCT_STOCK_NULL.getMsg());
//        商品库存不足
        Integer result =stock - quantity;
        if(result < 0) throw new ShopException(ResponseEnum.PRODUCT_STOCK_EMPTY.getMsg());
        productInfo.setProductStock(result);
        Boolean flag = productInfoService.updateById(productInfo);
        if(!flag) throw new ShopException(ResponseEnum.PRODUCT_SUBSTOCK_ERROR.getMsg());
        return flag;
    }
}
