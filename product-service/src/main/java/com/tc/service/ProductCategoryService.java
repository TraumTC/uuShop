package com.tc.service;

import com.tc.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tc.vo.ResultVO;

/**
 * <p>
 * 类目表 服务类
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
public interface ProductCategoryService extends IService<ProductCategory> {
    public ResultVO categoryList();

}
