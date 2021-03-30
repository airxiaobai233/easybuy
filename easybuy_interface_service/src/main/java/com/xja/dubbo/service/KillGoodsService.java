package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuySeckillGoods;
import com.xja.dubbo.entity.EasybuySeckillOrder;

import java.util.List;
import java.util.Map;

public interface KillGoodsService {
    //根据商品id查询秒杀商品
    EasybuySeckillGoods selectKillGoodsByGid(Integer gid)throws Exception;
    //查询正在执行的当前秒杀的商品列表
    List<EasybuySeckillGoods> selectNowKillGoods()throws Exception;
    //抢购单个商品
    public Map<String,Object> buyKillGoods(Integer gid, Integer uid) throws Exception;

    //根据用户ID，获取用户的所有抢购的商品列表
    public List<EasybuySeckillOrder> selectKillOrdersByUid(Integer uid) throws Exception;

    //支付方法从redis中获取需要支付的订单数据，支付完成以后插入到mysql中，同时删除redis中的秒杀订单数据
    public void addPayOrder(EasybuySeckillOrder pkillorder) throws Exception;

    //超时订单的处理
    public void deleteUserKillOrder(Integer gid, Integer uid) throws Exception;

    //根据用户ID和商品ID，获取该用户的订单信息
    public EasybuySeckillOrder selectKillOrderByUiuGid(Integer uid,Integer gid) throws Exception;

    //从mysql中获取订单信息
    public EasybuySeckillOrder selectKillOrderByOid(String oid)throws Exception;
    public void updateKillOrderByOid(EasybuySeckillOrder order)throws Exception;


    public void test();

}
