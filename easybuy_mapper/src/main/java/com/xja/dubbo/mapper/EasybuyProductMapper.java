package com.xja.dubbo.mapper;

import com.xja.dubbo.entity.EasybuyNews;
import com.xja.dubbo.entity.EasybuyProduct;

import java.util.List;

public interface EasybuyProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EasybuyProduct record);

    int insertSelective(EasybuyProduct record);

    EasybuyProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EasybuyProduct record);

    int updateByPrimaryKey(EasybuyProduct record);

    //查询所有的商品,
    List<EasybuyProduct> selectProds();


}