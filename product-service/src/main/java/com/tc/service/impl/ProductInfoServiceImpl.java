package com.tc.service.impl;

import com.tc.entity.ProductInfo;
import com.tc.mapper.ProductInfoMapper;
import com.tc.service.ProductInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
