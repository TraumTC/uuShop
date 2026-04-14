package com.tc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.tc.entity.ProductCategory;
import com.tc.entity.ProductInfo;
import com.tc.mapper.ProductCategoryMapper;
import com.tc.mapper.ProductInfoMapper;
import com.tc.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tc.vo.CategoryVo;
import com.tc.vo.InfoVo;
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
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(productCategory,categoryVo);
//            categoryVo.setName(productCategory.getCategoryName());
//            categoryVo.setType(productCategory.getCategoryType());

            List<InfoVo> infoVoList = new ArrayList<>();
//            条件查询  根据商品类型
            QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("category_type",productCategory.getCategoryType());
            List<ProductInfo> productInfoList = this.productInfoMapper.selectList(queryWrapper);
            for (ProductInfo productInfo : productInfoList) {
                InfoVo infoVo = new InfoVo();
//                两个对象 将productInfo对象中的属性复制给infoVo对象中的同名属性
                BeanUtils.copyProperties(productInfo,infoVo);
//                infoVo.setId(productInfo.getProductId());
//                infoVo.setName(productInfo.getProductName());
//                infoVo.setPrice(productInfo.getProductPrice());
//                infoVo.setDescription(productInfo.getProductDescription());
//                infoVo.setIcon(productInfo.getProductIcon());
//                infoVo.setStock(productInfo.getProductStock());
                infoVo.setQuantity(0);
                infoVoList.add(infoVo);
            }
            categoryVo.setGoods(infoVoList);
            categoryVoList.add(categoryVo);

            resultVO.setCode(0);
            resultVO.setMsg("成功");
            resultVO.setData(categoryVoList);
        }
        return resultVO;
    }
}
