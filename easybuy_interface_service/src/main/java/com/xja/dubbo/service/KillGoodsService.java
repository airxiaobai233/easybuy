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


}
