package com.tc.service.impl;

import com.tc.entity.ProductCategory;
import com.tc.mapper.ProductCategoryMapper;
import com.tc.service.ProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
