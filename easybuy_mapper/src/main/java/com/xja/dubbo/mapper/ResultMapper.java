package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.Result;

public interface ResultMapper {
    int insert(Result record);

    int insertSelective(Result record);
}