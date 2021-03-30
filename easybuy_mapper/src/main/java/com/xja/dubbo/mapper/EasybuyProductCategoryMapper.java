package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuyProductCategory;

import java.util.List;
import java.util.Map;

public interface EasybuyProductCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EasybuyProductCategory record);

    int insertSelective(EasybuyProductCategory record);

    EasybuyProductCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EasybuyProductCategory record);

    int updateByPrimaryKey(EasybuyProductCategory record);

    //查询商品的一级菜单信息
    public List<EasybuyProductCategory> selectFirstCategory();
}