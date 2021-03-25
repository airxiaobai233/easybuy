package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuySeckillGoods;

import java.util.List;

public interface EasybuySeckillGoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EasybuySeckillGoods record);

    int insertSelective(EasybuySeckillGoods record);

    EasybuySeckillGoods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EasybuySeckillGoods record);

    int updateByPrimaryKey(EasybuySeckillGoods record);

    //查询正在执行的当前秒杀的商品列表
    List<EasybuySeckillGoods> selectNowKillGoods();
}