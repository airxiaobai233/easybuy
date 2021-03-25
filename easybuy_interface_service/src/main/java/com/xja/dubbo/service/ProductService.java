package com.xja.dubbo.service;

import com.github.pagehelper.Page;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.entity.EasybuyUser;

import java.util.Map;

public interface ProductService {
    public Page<EasybuyProduct> selectProds(Map pmap)throws Exception;
    public void addCarToRedis(EasybuyUser loginuser, EasybuyProduct buyProd, int buyNum) throws  Exception;
    public EasybuyProduct selectById(Integer pid) throws  Exception;
}
