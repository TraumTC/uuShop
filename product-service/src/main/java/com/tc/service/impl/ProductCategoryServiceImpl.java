package com.tc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tc.entity.ProductCategory;
import com.tc.entity.ProductInfo;
import com.tc.mapper.ProductCategoryMapper;
import com.tc.mapper.ProductInfoMapper;
import com.tc.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tc.vo.ProductCategoryVo;
import com.tc.vo.ProductInfoVo;
import com.tc.vo.ResultVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 类目表 服务实现类
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    @Resource
    private ProductCategoryMapper productCategoryMapper;
    @Resource
    private ProductInfoMapper productInfoMapper;
    @Override
    public ResultVO categoryList() {
        ResultVO resultVO = new ResultVO();
        List<ProductCategory> productCategoryList = this.productCategoryMapper.selectList(null);
        List<ProductCategoryVo> productCategoryVoList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            ProductCategoryVo productCategoryVo = new ProductCategoryVo();
            BeanUtils.copyProperties(productCategory, productCategoryVo);
//            productCategoryVo.setName(productCategory.getCategoryName());
//            productCategoryVo.setType(productCategory.getCategoryType());

            List<ProductInfoVo> productInfoVoList = new ArrayList<>();
//            条件查询  根据商品类型
            QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("category_type",productCategory.getCategoryType());
            List<ProductInfo> productInfoList = this.productInfoMapper.selectList(queryWrapper);
            for (ProductInfo productInfo : productInfoList) {
                ProductInfoVo productInfoVo = new ProductInfoVo();
//                两个对象 将productInfo对象中的属性复制给infoVo对象中的同名属性
                BeanUtils.copyProperties(productInfo, productInfoVo);
//                productInfoVo.setId(productInfo.getProductId());
//                productInfoVo.setName(productInfo.getProductName());
//                productInfoVo.setPrice(productInfo.getProductPrice());
//                productInfoVo.setDescription(productInfo.getProductDescription());
//                productInfoVo.setIcon(productInfo.getProductIcon());
//                productInfoVo.setStock(productInfo.getProductStock());
                productInfoVo.setQuantity(0);
                productInfoVoList.add(productInfoVo);
            }
            productCategoryVo.setGoods(productInfoVoList);
            productCategoryVoList.add(productCategoryVo);

            resultVO.setCode(0);
            resultVO.setMsg("成功");
            resultVO.setData(productCategoryVoList);
        }
        return resultVO;
    }
}
