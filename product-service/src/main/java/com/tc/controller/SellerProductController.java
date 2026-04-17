package com.tc.controller;

import com.tc.util.ResultVOUtil;
import com.tc.vo.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/findAllProductCategory")
    public ResultVO findAllProductCategory() {


        return ResultVOUtil.success(null);
    }

}
