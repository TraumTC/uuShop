package com.tc.service;

import com.tc.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tc.vo.CategoryVo;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.List;

/**
 * <p>
 * 类目表 服务类
 * </p>
 *
 * @author tc
 * @since 2026-04-11
 */
public interface ProductCategoryService extends IService<ProductCategory> {
    public List<CategoryVo> categoryList();

}
