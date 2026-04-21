package com.tc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tc.entity.ProductInfo;
import com.tc.vo.ProductExcelVO;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
public interface ProductInfoService extends IService<ProductInfo> {
      public List<ProductExcelVO> productExcelVOList();
    public List<ProductInfo> excleToProductInfoList(InputStream inputStream);
}
