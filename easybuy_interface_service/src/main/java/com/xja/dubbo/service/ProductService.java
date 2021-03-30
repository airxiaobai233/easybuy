package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.entity.EasybuyProductCategory;
import com.xja.dubbo.entity.EasybuyUser;

import java.util.List;
import java.util.Map;

public interface ProductService {
    public Map<String,Object> selectProds(Integer npage,EasybuyProduct product)throws Exception;
    public void addCarToRedis(EasybuyUser loginuser, EasybuyProduct buyProd, int buyNum) throws  Exception;
    public EasybuyProduct selectById(Integer pid) throws  Exception;

    //根据条件查询相关的对象
    public Map<String,Object> selectProdsBySearch(Integer npage , EasybuyProduct product)  throws Exception;



}
