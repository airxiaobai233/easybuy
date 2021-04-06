package com.xja.dubbo.service.impl;

import com.xja.dubbo.entity.EasybuyProductImages;
import com.xja.dubbo.mapper.EasybuyProductImagesMapper;
import com.xja.dubbo.service.ProductImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productImagesService")
public class ProductImagesServiceImpl implements ProductImagesService {
    @Autowired
    EasybuyProductImagesMapper productImagesMapper;

    @Override
    public List<EasybuyProductImages> selectImagesByPid(Integer pid) {
        return productImagesMapper.selectImagesByPid(pid);
    }
}
