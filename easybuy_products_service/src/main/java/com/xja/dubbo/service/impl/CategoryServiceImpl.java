package com.xja.dubbo.service.impl;


import com.xja.dubbo.entity.EasybuyProductCategory;
import com.xja.dubbo.mapper.EasybuyProductCategoryMapper;
import com.xja.dubbo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    EasybuyProductCategoryMapper productCategoryMapper;
    @Override
    public List<EasybuyProductCategory> selectFirstCategory() {
        List<EasybuyProductCategory> categoryList = redisTemplate.boundListOps("firstLevalCategoryList").range(0, -1);
        if (categoryList==null || categoryList.size()==0){
            categoryList = productCategoryMapper.selectFirstCategory();
            for (EasybuyProductCategory category:categoryList){
                redisTemplate.boundListOps("firstLevalCategoryList").leftPush(category);
            }
        }
        return categoryList;
    }
}
