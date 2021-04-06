package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuyProductImages;

import java.util.List;

public interface ProductImagesService {
    //根据pid查找对应的图片信息
    List<EasybuyProductImages> selectImagesByPid(Integer pid);
}
