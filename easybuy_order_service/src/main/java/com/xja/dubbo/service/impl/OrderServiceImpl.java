package com.xja.dubbo.service.impl;

import com.xja.dubbo.entity.EasybuyOrder;
import com.xja.dubbo.entity.EasybuyOrderDetail;
import com.xja.dubbo.entity.EasybuySeckillOrder;
import com.xja.dubbo.mapper.EasybuyOrderDetailMapper;
import com.xja.dubbo.mapper.EasybuyOrderMapper;
import com.xja.dubbo.service.OrderService;
import com.xja.dubbo.utils.JsonUtil;
import org.noggit.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;


import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicTextDestination;
    @Autowired
    private EasybuyOrderMapper easybuyOrderMapper;
    @Autowired
    private EasybuyOrderDetailMapper easybuyOrderDetailMapper;

    @Override
    public void addOrder(EasybuyOrder order, List<EasybuyOrderDetail> orderDetailList) throws Exception {
        System.out.println("进来了");
        //添加订单对象，同时完成订单明细的添加
        easybuyOrderMapper.insertSelective(order);
        for(EasybuyOrderDetail detail :orderDetailList){
            //建立订单和订单明细的关系
            detail.setOrderid(order.getId());
            easybuyOrderDetailMapper.insertSelective(detail);
        }
        System.out.println("快到有问题的地方了");
        final HashMap<String,Object> messageMap = new HashMap<String,Object>();
        order.setCreatetime(null);
        messageMap.put("order",order);
        messageMap.put("orderDetailList",orderDetailList);
        System.out.println("messageMap"+messageMap);
        //发送一个消息
        jmsTemplate.send(topicTextDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("发送了一条消息");
                return session.createObjectMessage(messageMap);

            }
        });
        //发送消息完毕

    }
}
