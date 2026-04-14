package com.tc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tc.entity.OrderDetail;
import com.tc.entity.OrderMaster;
import com.tc.entity.ProductInfo;
import com.tc.excepion.ShopException;
import com.tc.feign.ProductFeign;
import com.tc.from.BuyerOrderForm;
import com.tc.from.BuyerOrderInnerForm;
import com.tc.mapper.ProductInfoMapper;
import com.tc.result.ResponseEnum;
import com.tc.service.OrderDetailService;
import com.tc.service.OrderMasterService;
import com.tc.util.ResultVOUtil;
import com.tc.vo.BuyerOrderDetailVO;
import com.tc.vo.BuyerOrderMasterVO;
import com.tc.vo.ResultVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单详情表 前端控制器
 * </p>
 *
 * @author tc
 * @since 2026-04-13
 */
@RestController
@RequestMapping("/buyer/order")
public class BuyerOrderController {
    @Resource
    private ProductFeign productFeign;
    @Resource
    private ProductInfoMapper productInfoMapper;
    @Resource
    private OrderMasterService orderMasterService;
    @Resource
    private OrderDetailService orderDetailService;

    @PostMapping("/create")
    public ResultVO create(@RequestBody BuyerOrderForm from){
//        总订单
        List<BuyerOrderInnerForm> items = from.getItems();
        BigDecimal orderAmount = new BigDecimal(0);
//        算总价
        for (BuyerOrderInnerForm item : items) {
            Integer productId = item.getProductId();
            Integer productQuantity = item.getProductQuantity();
            BigDecimal price = this.productFeign.findPriceById(productId);
            BigDecimal totalPrice = price.multiply(new BigDecimal(productQuantity));
            orderAmount = orderAmount.add(totalPrice);
        }
        OrderMaster orderMaster = new OrderMaster();
//        买家ID
        orderMaster.setBuyerOpenid(from.getId());
        orderMaster.setBuyerName(from.getName());
        orderMaster.setBuyerPhone(from.getPhone());
        orderMaster.setBuyerAddress(from.getAddress());
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(0);
        orderMaster.setPayStatus(0);
//        插入数据
        boolean flag = this.orderMasterService.save(orderMaster);
        if (!flag) throw new ShopException(ResponseEnum.ORDER_CREATE_ERROR.getMsg());
//        单个商品订单
        for(BuyerOrderInnerForm item : items){
            OrderDetail  orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderMaster.getOrderId());
            orderDetail.setProductId(item.getProductId());
            orderDetail.setProductQuantity(item.getProductQuantity());
            ProductInfo  productInfo = this.productInfoMapper.selectById(item.getProductId());
            orderDetail.setProductIcon(productInfo.getProductIcon());
            orderDetail.setProductName(productInfo.getProductName());
            orderDetail.setProductPrice(productInfo.getProductPrice());
            orderDetail.setProductQuantity(item.getProductQuantity());
            boolean flag2 = this.orderDetailService.save(orderDetail);
            Boolean flag3 = this.productFeign.subStockById(item.getProductId(), item.getProductQuantity());
            if(!flag2 || !flag3) throw new ShopException(ResponseEnum.ORDER_CREATE_ERROR.getMsg());
        }
        Map map=new HashMap<>();
        map.put("orderId",orderMaster.getOrderId());
        return ResultVOUtil.success(map);
    }
    @GetMapping("/list/{buyerId}/{page}/{size}")
    public ResultVO list(@PathVariable("buyerId") Integer id,
                         @PathVariable("page") Integer page,
                         @PathVariable("size") Integer size){
//        Page<T>是MyBatis-Plus 提供的分页实体类
        Page<OrderMaster> pageModel = new Page<>(page,size);
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_openid",id);
        Page<OrderMaster> page1 = this.orderMasterService.page(pageModel, queryWrapper);
        List<OrderMaster> records = page1.getRecords();
        return ResultVOUtil.success(records);
    }

    @GetMapping("/detail/{buyerId}/{orderId}")
    public ResultVO detail(@PathVariable("buyerId") Integer buyerId,
                           @PathVariable("orderId") String orderId){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        queryWrapper.eq("buyer_openid",buyerId);
        OrderMaster orderMaster = this.orderMasterService.getOne(queryWrapper);
        BuyerOrderMasterVO buyerOrderMasterVO = new BuyerOrderMasterVO();
        BeanUtils.copyProperties(orderMaster,buyerOrderMasterVO);
        QueryWrapper<OrderDetail> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("order_id",orderMaster.getOrderId());

        List<OrderDetail> orderDetailList = this.orderDetailService.list(queryWrapper1);
        List< BuyerOrderDetailVO> buyerOrderDetailVOList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            BuyerOrderDetailVO buyerOrderDetailVO = new BuyerOrderDetailVO();
            BeanUtils.copyProperties(orderDetail,buyerOrderDetailVO);
            buyerOrderDetailVOList.add(buyerOrderDetailVO);
        }
        buyerOrderMasterVO.setBuyerOrderDetailVOList(buyerOrderDetailVOList);
        return ResultVOUtil.success(buyerOrderMasterVO);
    }

    @PutMapping("/cancel/{buyerId}/{orderId}")
    public ResultVO cancel(@PathVariable("buyerId") Integer buyerId,
                           @PathVariable("orderId") String orderId){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        queryWrapper.eq("buyer_openid",buyerId);
        OrderMaster orderMaster = this.orderMasterService.getOne(queryWrapper);
        if(orderMaster == null) throw new ShopException(ResponseEnum.ORDER_NULL.getMsg());
        if(! orderMaster.getOrderStatus().equals(0)) throw new ShopException(ResponseEnum.ORDER_STATUS_ERROR.getMsg());
        orderMaster.setOrderStatus(2);
        boolean flag = this.orderMasterService.updateById(orderMaster);
        if(!flag) throw new ShopException(ResponseEnum.ORDER_CANCEL_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }
    @PutMapping("/finish/{orderId}")
    public ResultVO finish(@PathVariable("orderId") String orderId){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = this.orderMasterService.getOne(queryWrapper);
        if(orderMaster == null) throw new ShopException(ResponseEnum.ORDER_NULL.getMsg());
        if(! orderMaster.getOrderStatus().equals(0)) throw new ShopException(ResponseEnum.ORDER_STATUS_ERROR.getMsg());
        orderMaster.setOrderStatus(1);
        boolean flag = this.orderMasterService.updateById(orderMaster);
        if(!flag) throw new ShopException(ResponseEnum.ORDER_FINISH_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }
    @PutMapping("/pay/{buyerId}/{orderId}")
    public ResultVO pay(@PathVariable("buyerId") Integer buyerId,
                        @PathVariable("orderId") String orderId ){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_openid",buyerId);
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = this.orderMasterService.getOne(queryWrapper);
        if(orderMaster == null) throw new ShopException(ResponseEnum.ORDER_NULL.getMsg());
        if(! orderMaster.getOrderStatus().equals(0)) throw new ShopException(ResponseEnum.ORDER_STATUS_ERROR.getMsg());
        if( orderMaster.getPayStatus().equals(1)) throw new ShopException(ResponseEnum.ORDER_PAY_ERROR.getMsg());
        orderMaster.setPayStatus(1);
        boolean flag = this.orderMasterService.updateById(orderMaster);
        if(!flag) throw new ShopException(ResponseEnum.ORDER_PAY_FAIL.getMsg());
        return ResultVOUtil.success(null);
    }
}
