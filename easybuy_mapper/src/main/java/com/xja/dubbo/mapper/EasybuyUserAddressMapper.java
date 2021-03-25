package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuyUserAddress;

public interface EasybuyUserAddressMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EasybuyUserAddress record);

    int insertSelective(EasybuyUserAddress record);

    EasybuyUserAddress selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EasybuyUserAddress record);

    int updateByPrimaryKey(EasybuyUserAddress record);
}