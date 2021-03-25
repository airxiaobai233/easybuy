package com.xja.dubbo.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xja.dubbo.entity.CarItem;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.mapper.EasybuyProductMapper;
import com.xja.dubbo.service.ProductService;
import com.xja.dubbo.utils.PageSizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    EasybuyProductMapper easybuyProductMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Page<EasybuyProduct> selectProds(Map pmap) throws Exception {
        PageHelper.startPage(Integer.parseInt(pmap.get("npage").toString()), PageSizeUtil.PRODUCT_PAGE_SIZE);
        Page<EasybuyProduct> prodsPage = (Page<EasybuyProduct>) easybuyProductMapper.selectProds();
        return prodsPage;
    }

    @Override
    public void addCarToRedis(EasybuyUser loginuser, EasybuyProduct buyProd, int buyNum) throws Exception {
        //从redis中获取该用户的购物车列表信息
        CarItem carItem = null;
        if(redisTemplate.boundHashOps("user_car_"+loginuser.getId()).hasKey(buyProd.getId()+"")){
            carItem = (CarItem) redisTemplate.boundHashOps("user_car_" + loginuser.getId()).get(buyProd.getId() + "");
            carItem.setBuyNum(carItem.getBuyNum()+1);
        }else {
            carItem = new CarItem(buyProd,buyNum);
        }
        redisTemplate.boundHashOps("user_car_"+loginuser.getId()).put( buyProd.getId()+"",carItem);
        System.out.println("把购物车的数据保存到redis中");
    }

    @Override
    public EasybuyProduct selectById(Integer pid) throws Exception {
        return easybuyProductMapper.selectByPrimaryKey(pid);
    }


}
