package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuyOrder;

public interface EasybuyOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EasybuyOrder record);

    int insertSelective(EasybuyOrder record);

    EasybuyOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EasybuyOrder record);

    int updateByPrimaryKey(EasybuyOrder record);
}