package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuyUser;

public interface EasybuyUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EasybuyUser record);

    int insertSelective(EasybuyUser record);

    EasybuyUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EasybuyUser record);

    int updateByPrimaryKey(EasybuyUser record);

    //根据用户名查找用户信息
    public EasybuyUser selectUserByName(String loginName);
}