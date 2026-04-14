package com.tc.service.impl;

import com.tc.entity.OrderDetail;
import com.tc.mapper.OrderDetailMapper;
import com.tc.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author tc
 * @since 2026-04-13
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
