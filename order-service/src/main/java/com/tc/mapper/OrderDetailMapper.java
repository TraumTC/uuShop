package com.tc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tc.entity.OrderDetail;
import com.tc.vo.BarData;
import com.tc.vo.BasicLineData;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 订单详情表 Mapper 接口
 * </p>
 *
 * @author tc
 * @since 2026-04-13
 */
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    @Select({"select product_name name,sum(product_quantity) value from order_detail group by product_name"})
    public List<BarData> barSale();

    @Select({"select date_format(create_time,'%Y-%m-%d') name,sum(product_quantity) value from order_detail group by date_format(create_time,'%Y-%m-%d')"})
    public List<BasicLineData> basicLine();

    @Select({"select distinct DATE_FORMAT(order_detail.create_time, '%Y-%m-%d') as dd from order_detail"})
    public List<String> getDate();

    @Select({"select product_name from product_info"})
    public List<String> getNames();

    @Select({"select (\n" +
            "    select COALESCE(sum(product_quantity),0)\n" +
            "    from order_detail where\n" +
            "    pi.product_id = order_detail.product_id and\n" +
            "    DATE_FORMAT(order_detail.create_time, '%Y-%m-%d') = mm.dd\n" +
            "    ) as count\n" +
            "from product_info pi,\n" +
            "     (select distinct DATE_FORMAT(order_detail.create_time, '%Y-%m-%d') as dd from order_detail)\n" +
            "         as mm\n" +
            "where pi.product_name = #{productName} order by mm.dd"})
    public List<Integer> getData(String productName);
}
