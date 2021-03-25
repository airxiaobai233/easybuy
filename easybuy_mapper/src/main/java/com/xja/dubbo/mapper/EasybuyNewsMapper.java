package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuyNews;

import java.util.List;

public interface EasybuyNewsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EasybuyNews record);

    int insertSelective(EasybuyNews record);

    EasybuyNews selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EasybuyNews record);

    int updateByPrimaryKey(EasybuyNews record);

    //获取所有的新闻
    List<EasybuyNews> selectNews();

}