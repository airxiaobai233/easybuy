package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuyProductComment;

import java.util.Map;

public interface ProductCommentService {
    //根据商品id查找它的评价列表
    public Map<String,Object> selectCommentByPid(Integer pid,Integer npage) throws Exception;
    //统计各种评论数
    Map<String,Object> selectLevelCommentNumByPid(Integer pid);
}
