package com.xja.dubbo.service;

import com.github.pagehelper.Page;
import com.xja.dubbo.entity.EasybuyNews;


public interface NewsService {
    public Page<EasybuyNews> selectNews();
}
