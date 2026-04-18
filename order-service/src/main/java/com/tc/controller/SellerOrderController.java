package com.tc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tc.entity.OrderDetail;
import com.tc.entity.OrderMaster;
import com.tc.excepion.ShopException;
import com.tc.feign.ProductFeign;
import com.tc.mapper.OrderDetailMapper;
import com.tc.result.ResponseEnum;
import com.tc.service.OrderDetailService;
import com.tc.service.OrderMasterService;
import com.tc.util.EChartsColorUtil;
import com.tc.util.ResultVOUtil;
import com.tc.vo.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author tc
 * @since 2026-04-13
 */
@RestController
@RequestMapping("/seller/order")
public class SellerOrderController {

    @Resource
    private OrderMasterService orderMasterService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private ProductFeign productFeign;

    @GetMapping("/list/{page}/{size}")
    public ResultVO list(@PathVariable("page") Integer page,
                         @PathVariable("size") Integer size) {
        Page<OrderMaster> pageModel = new Page<>(page, size);
        Page<OrderMaster> resultPage = this.orderMasterService.page(pageModel,null);
        PageVO pageVO = new PageVO();
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setSize(resultPage.getSize());
        pageVO.setContent(resultPage.getRecords());

        return ResultVOUtil.success(pageVO);
    }

    @PutMapping("/cancel/{orderId}")
    public ResultVO cancel(@PathVariable("orderId") Integer orderId) {
        QueryWrapper <OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderMaster orderMaster = this.orderMasterService.getOne(queryWrapper);
        if (orderMaster == null) throw new ShopException(ResponseEnum.ORDER_NOT_EXIST.getMsg());
        Integer status = orderMaster.getOrderStatus();
        if(status == 1) throw new ShopException(ResponseEnum.ORDER_CANCEL_FINISHED.getMsg());
        if(status == 2) throw new ShopException(ResponseEnum.ORDER_CANCELED.getMsg());
        orderMaster.setOrderStatus(2);
        boolean updateStatus = this.orderMasterService.updateById(orderMaster);
        if(updateStatus) throw new ShopException(ResponseEnum.ORDER_CANCEL_ERROR.getMsg());
//        恢复库存
        QueryWrapper <OrderDetail> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("order_id", orderId);
        List<OrderDetail> list = this.orderDetailService.list(queryWrapper1);
        for (OrderDetail orderDetail : list) {
            Integer productId = orderDetail.getProductId();
            Integer productQuantity = orderDetail.getProductQuantity();
            Boolean flag = this.productFeign.subStockById(productId, -productQuantity);
        }
        return ResultVOUtil.success(null);
    }

    @PutMapping("/finish/{orderId}")
    public ResultVO finish(@PathVariable("orderId") Integer orderId) {
        QueryWrapper <OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderMaster orderMaster = this.orderMasterService.getOne(queryWrapper);
        if (orderMaster == null) throw new ShopException(ResponseEnum.ORDER_NOT_EXIST.getMsg());
        Integer status = orderMaster.getOrderStatus();
        if(status == 1) throw new ShopException(ResponseEnum.ORDER_FINISHED.getMsg());
        if(status == 2) throw new ShopException(ResponseEnum.ORDER_FINISH_CANCELED.getMsg());
        orderMaster.setOrderStatus(2);
        boolean updateStatus = this.orderMasterService.updateById(orderMaster);
        if(updateStatus) throw new ShopException(ResponseEnum.ORDER_FINISH_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }
    @PutMapping("/barSale")
    public ResultVO barSale() {
        List<BarData> barData = this.orderDetailMapper.barSale();
        BarVO barVO = new BarVO();
        List<String> names = new ArrayList<>();
        List<BarInnerVO> values = new ArrayList<>();
        for (BarData barData1 : barData) {
            names.add(barData1.getName());
            BarInnerVO  barInnerVO= new BarInnerVO();
            Integer value = barData1.getValue();
            barInnerVO.setValue(value);
            barInnerVO.setItemStyle(EChartsColorUtil.createItemStyle(value));
            values.add(barInnerVO);
        }
        barVO.setNames(names);
        barVO.setValues(values);
        return  ResultVOUtil.success(barVO);
    }
    @PutMapping("/basicLineSale")
    public ResultVO basicLineSale() {
        List<BasicLineData> basicLineData = this.orderDetailMapper.basicLine();
        BasicLineVO basicLineVO = new BasicLineVO();
        List<String> names = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (BasicLineData basicLineData1 : basicLineData) {
            String name = basicLineData1.getName();
            Integer value = basicLineData1.getValue();
            names.add(name);
            values.add(value);
        }
        basicLineVO.setNames(names);
        basicLineVO.setValues(values);
        return  ResultVOUtil.success(basicLineVO);
    }
    @PutMapping("/stackedLineSale")
    public ResultVO stackedLineSale() {

        return  ResultVOUtil.success(null);
    }
}
