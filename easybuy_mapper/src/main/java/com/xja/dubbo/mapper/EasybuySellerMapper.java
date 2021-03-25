package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuySeller;

public interface EasybuySellerMapper {
    int deleteByPrimaryKey(String sellerId);

    int insert(EasybuySeller record);

    int insertSelective(EasybuySeller record);

    EasybuySeller selectByPrimaryKey(String sellerId);

    int updateByPrimaryKeySelective(EasybuySeller record);

    int updateByPrimaryKey(EasybuySeller record);
}