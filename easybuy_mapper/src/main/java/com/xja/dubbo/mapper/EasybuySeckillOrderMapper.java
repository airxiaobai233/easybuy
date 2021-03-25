package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuySeckillOrder;

public interface EasybuySeckillOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EasybuySeckillOrder record);

    int insertSelective(EasybuySeckillOrder record);

    EasybuySeckillOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EasybuySeckillOrder record);

    int updateByPrimaryKey(EasybuySeckillOrder record);
}