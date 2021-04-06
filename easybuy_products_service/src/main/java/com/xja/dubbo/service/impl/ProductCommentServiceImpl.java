package com.xja.dubbo.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xja.dubbo.entity.EasybuyProductComment;
import com.xja.dubbo.mapper.EasybuyProductCommentMapper;
import com.xja.dubbo.service.ProductCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("productCommentService")
public class ProductCommentServiceImpl implements ProductCommentService {
    @Autowired
    EasybuyProductCommentMapper easybuyProductCommentMapper;


    @Override
    public Map<String, Object> selectCommentByPid(Integer pid,Integer npage) throws Exception {
        PageHelper.startPage(npage,2);
        Page<EasybuyProductComment> commentPage = (Page<EasybuyProductComment>) easybuyProductCommentMapper.selectCommentByPid(pid);
        Map<String,Object> resuMap = new HashMap<String, Object>();
        resuMap.put("commentList",commentPage.getResult());
        resuMap.put("pageSize",commentPage.getPages());
        resuMap.put("commentCount",commentPage.getTotal());
        return resuMap;
    }

    @Override
    public Map<String, Object> selectLevelCommentNumByPid(Integer pid) {
        return easybuyProductCommentMapper.selectLevelCommentNumByPid(pid);
    }

}

