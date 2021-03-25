package com.xja.dubbo.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xja.dubbo.entity.EasybuyNews;
import com.xja.dubbo.mapper.EasybuyNewsMapper;
import com.xja.dubbo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("newsService")
public class NewsServiceImpl implements NewsService {
    @Autowired
    EasybuyNewsMapper easybuyNewsMapper;
    @Override
    public Page<EasybuyNews> selectNews() {
        PageHelper.startPage(1,2);
        Page<EasybuyNews> newsPage = (Page<EasybuyNews>) easybuyNewsMapper.selectNews();
        return newsPage;
    }
}
