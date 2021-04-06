package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuyOrder;
import com.xja.dubbo.entity.EasybuyOrderDetail;
import com.xja.dubbo.entity.EasybuySeckillOrder;

import java.util.List;

public interface OrderService {
    //添加订单信息
    public abstract void addOrder(EasybuyOrder order, List<EasybuyOrderDetail> orderDetailList)throws Exception;
}
