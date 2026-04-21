package com.tc.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tc.entity.ProductInfo;
import com.tc.mapper.ProductCategoryMapper;
import com.tc.mapper.ProductInfoMapper;
import com.tc.service.ProductInfoService;
import com.tc.vo.ProductExcelVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {
 @Autowired
    private ProductInfoMapper productInfoMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

     @Override
    public List<ProductExcelVO> productExcelVOList() {
        List<ProductInfo> productInfoList = this.productInfoMapper.selectList(null);
        List<ProductExcelVO> productExcelVOList = new ArrayList<>();
        for (ProductInfo productInfo : productInfoList) {
            ProductExcelVO productExcelVO = new ProductExcelVO();
            BeanUtils.copyProperties(productInfo, productExcelVO);
            if(productInfo.getProductStatus() == 1) {
                productExcelVO.setProductStatus("正常");
            }else{
                productExcelVO.setProductStatus("下架");
            }
            productExcelVO.setCategoryName(this.productCategoryMapper.findCategoryNameByType(productInfo.getCategoryType()));
            productExcelVOList.add(productExcelVO);
        }
        return productExcelVOList;
    }

    @Override
    public List<ProductInfo> excleToProductInfoList(InputStream inputStream) {
        try {
            List<ProductInfo> list = new ArrayList<>();
            EasyExcel.read(inputStream)
                    .head(ProductExcelVO.class)
                    .sheet()
                    .registerReadListener(new AnalysisEventListener<ProductExcelVO>() {

                        @Override
                        public void invoke(ProductExcelVO excelData, AnalysisContext analysisContext) {
                            ProductInfo productInfo = new ProductInfo();
                            BeanUtils.copyProperties(excelData, productInfo);
                            if(excelData.getProductStatus().equals("正常")){
                                productInfo.setProductStatus(1);
                            }else{
                                productInfo.setProductStatus(0);
                            }
                            list.add(productInfo);
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                            System.out.println("=========================文件解析完成=========================");
                        }
                    }).doRead();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
