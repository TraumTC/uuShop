package com.tc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tc.entity.ProductCategory;
import com.tc.entity.ProductInfo;
import com.tc.excepion.ShopException;
import com.tc.form.SellerProductAddForm;
import com.tc.form.SellerProductUpdateForm;
import com.tc.result.ResponseEnum;
import com.tc.service.ProductCategoryService;
import com.tc.service.ProductInfoService;
import com.tc.util.ResultVOUtil;
import com.tc.vo.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
@RestController
@RequestMapping("/seller/product")
public class SellerProductController {
    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private ProductInfoService productInfoService;
    @GetMapping("/findAllProductCategory")
    public ResultVO findAllProductCategory() {
        List<ProductCategory> productCategoryList = this.productCategoryService.list();
        List<SellerProductCategoryVO> sellerProductCategoryVOList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            SellerProductCategoryVO sellerProductCategoryVO = new SellerProductCategoryVO();
            sellerProductCategoryVO.setName(productCategory.getCategoryName());
            sellerProductCategoryVO.setType(productCategory.getCategoryType());
            sellerProductCategoryVOList.add(sellerProductCategoryVO);
        }
        Map map = new HashMap<>();
        map.put("content", sellerProductCategoryVOList);

        return ResultVOUtil.success(map);
    }
    @PostMapping("/add")
    public ResultVO add(@RequestBody SellerProductAddForm Form) {
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(Form, productInfo);
        boolean save = this.productInfoService.save(productInfo);
        if (!save) throw new ShopException(ResponseEnum.PRODUCT_ADD_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }
//查询商品
    @GetMapping("/list/{page}/{size}")
    public ResultVO list(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        Page<ProductInfo> pageModel = new Page<>(page, size);
        Page<ProductInfo> resultPage = this.productInfoService.page(pageModel, null);
        PageVO vo = new PageVO();
        vo.setSize(resultPage.getSize());
        vo.setTotal(resultPage.getTotal());
        List<SellerProductInfoVO> voList = new ArrayList<>();
        List<ProductInfo> list = resultPage.getRecords();
        for (ProductInfo productInfo : list) {
            SellerProductInfoVO vo1 = new SellerProductInfoVO();
            Integer categoryType = productInfo.getCategoryType();
            QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("category_type", categoryType);
            ProductCategory productCategory = this.productCategoryService.getOne(queryWrapper);
            vo1.setCategoryName(productCategory.getCategoryName());
            BeanUtils.copyProperties(productInfo, vo1);
            Integer productStatus = productInfo.getProductStatus();
            if(productStatus == 1){
                vo1.setStatus(true);
            }else {
                vo1.setStatus(false);
            }
            voList.add(vo1);
        }
        vo.setContent(voList);
        return ResultVOUtil.success(vo);
    }
//模糊查询商品
    @GetMapping("/like/{keyword}/{page}/{size}")
    public ResultVO like(@PathVariable("keyword" ) String keyword,
                         @PathVariable("page" ) Integer page,
                         @PathVariable("size") Integer size) {
        Page<ProductInfo> pageModel = new Page<>(page, size);
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("product_name", keyword);
        Page<ProductInfo> resultPage = this.productInfoService.page(pageModel, queryWrapper);
        PageVO vo = new PageVO();
        vo.setSize(resultPage.getSize());
        vo.setTotal(resultPage.getTotal());
        List<SellerProductInfoVO> voList = new ArrayList<>();
        List<ProductInfo> list = resultPage.getRecords();
        for (ProductInfo productInfo : list) {
            SellerProductInfoVO vo1 = new SellerProductInfoVO();
            Integer categoryType = productInfo.getCategoryType();
            QueryWrapper<ProductCategory> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("category_type", categoryType);
            ProductCategory productCategory = this.productCategoryService.getOne(queryWrapper1);
            vo1.setCategoryName(productCategory.getCategoryName());
            BeanUtils.copyProperties(productInfo, vo1);
            Integer productStatus = productInfo.getProductStatus();
            if(productStatus == 1){
                vo1.setStatus(true);
            }else {
                vo1.setStatus(false);
            }
            voList.add(vo1);
        }
        vo.setContent(voList);
        return ResultVOUtil.success(vo);
    }
//分类查询商品
    @GetMapping("/findByCategory/{categoryType}/{page}/{size}")
    public ResultVO findByCategory(@PathVariable("categoryType") Integer categoryType,
                                   @PathVariable("page") Integer page,
                                   @PathVariable("size") Integer size) {

        Page<ProductInfo> pageModel = new Page<>(page, size);
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_type", categoryType);
        Page<ProductInfo> resultPage = this.productInfoService.page(pageModel, queryWrapper);
        PageVO vo = new PageVO();
        vo.setSize(resultPage.getSize());
        vo.setTotal(resultPage.getTotal());
        List<SellerProductInfoVO> voList = new ArrayList<>();
        List<ProductInfo> list = resultPage.getRecords();
        QueryWrapper<ProductCategory> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("category_type", categoryType);
        ProductCategory productCategory = this.productCategoryService.getOne(queryWrapper1);
        String categoryName = productCategory.getCategoryName();
        for (ProductInfo productInfo : list) {
            SellerProductInfoVO vo1 = new SellerProductInfoVO();
            vo1.setCategoryName(categoryName);
            BeanUtils.copyProperties(productInfo, vo1);
            Integer productStatus = productInfo.getProductStatus();
            if(productStatus == 1){
                vo1.setStatus(true);
            }else {
                vo1.setStatus(false);
            }
            voList.add(vo1);
        }
        vo.setContent(voList);
        return ResultVOUtil.success(vo);
    }

    @GetMapping("/findById/{id}")
    public ResultVO findById(@PathVariable("id") Integer id) {
        if(id == null) throw new ShopException(ResponseEnum.ID_NULL.getMsg());
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", id);
        ProductInfo productInfo = this.productInfoService.getOne(queryWrapper);
        SellerFindIdVO vo = new SellerFindIdVO();
        BeanUtils.copyProperties(productInfo, vo);
        Map<String,Integer> map = new HashMap<>();
        map.put("categoryType", productInfo.getCategoryType());
        vo.setCategory(map);
        Integer productStatus = productInfo.getProductStatus();
        if(productStatus == 1){
            vo.setStatus(true);
        }else {
            vo.setStatus(false);
        }

        return ResultVOUtil.success(vo);
    }
    @DeleteMapping("/delete/{id}")
    public ResultVO delete(@PathVariable("id") Integer id) {
        if(id == null) throw new ShopException(ResponseEnum.ID_NULL.getMsg());
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", id);
        ProductInfo productInfo = this.productInfoService.getOne(queryWrapper);
        if(productInfo == null) throw new ShopException(ResponseEnum.PRODUCT_NOT_EXIST.getMsg());
        boolean remove = this.productInfoService.removeById(id);
        if(!remove) throw new ShopException(ResponseEnum.PRODUCT_DELETE_ERROR.getMsg());
        return  ResultVOUtil.success(null);
    }

    @PutMapping("/updateStatus/{id}/{status}")
    public ResultVO updateStatus(@PathVariable("id" ) Integer id,
                                 @PathVariable("status") Boolean status) {
        if(id == null) throw new ShopException(ResponseEnum.ID_NULL.getMsg());
        if(status == null) throw new ShopException(ResponseEnum.PRODUCT_STATUS_NULL.getMsg());
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", id);
        ProductInfo productInfo = this.productInfoService.getOne(queryWrapper);
        if(productInfo == null) throw new ShopException(ResponseEnum.PRODUCT_NOT_EXIST.getMsg());
        Integer productStatus = productInfo.getProductStatus();
        boolean flag = false;
        if(status && productStatus == 1){
            throw new ShopException(ResponseEnum.PRODUCT_STATUS_TRUE_ERROR.getMsg());
        }else if(!status && productStatus == 0){
            throw new ShopException(ResponseEnum.PRODUCT_STATUS_FALSE_ERROR.getMsg());
        }
        if(!status){
            productInfo.setProductStatus(0);
            flag = this.productInfoService.updateById(productInfo);
        }else if(status){
            productInfo.setProductStatus(1);
            flag = this.productInfoService.updateById(productInfo);
        }
        if(!flag) throw new ShopException(ResponseEnum.PRODUCT_STATUS_UPDATE_ERROR.getMsg());
        return ResultVOUtil.success(status);
    }

    @PutMapping("/update")
    public ResultVO update(@RequestBody SellerProductUpdateForm form) {
        Integer id = form.getId();
        ProductInfo productInfo = this.productInfoService.getById(id);
        if(productInfo == null) throw new ShopException(ResponseEnum.PRODUCT_NOT_EXIST.getMsg());
        productInfo.setProductId(form.getId());
        if(form.getStatus()){
            productInfo.setProductStatus(1);
        } else {
            productInfo.setProductStatus(0);
        }
        productInfo.setProductPrice(form.getPrice());
        productInfo.setProductName(form.getName());
        productInfo.setProductIcon(form.getIcon());
        productInfo.setProductDescription(form.getDescription());
        productInfo.setCategoryType(form.getCategory().getCategoryType());
        productInfo.setProductStock(form.getStock());
        boolean updateById = this.productInfoService.updateById(productInfo);
        if(!updateById) throw new ShopException(ResponseEnum.PRODUCT_UPDATE_ERROR.getMsg());

        return ResultVOUtil.success(null);
    }
    @PutMapping("/export")
    public void export(HttpServletResponse response) {


    }
    @PutMapping("/import")
    public ResultVO importExcel(HttpServletResponse response) {

        return ResultVOUtil.success(null);
    }
}
