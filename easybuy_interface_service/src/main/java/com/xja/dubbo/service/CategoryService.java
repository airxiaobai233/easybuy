package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuyProductCategory;

import java.util.List;

public interface CategoryService {
    //查询商品的一级菜单信息,nosql
    public List<EasybuyProductCategory> selectFirstCategory();
}
