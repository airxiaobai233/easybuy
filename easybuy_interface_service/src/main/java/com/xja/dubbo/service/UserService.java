package com.xja.dubbo.service;

import com.xja.dubbo.entity.EasybuyUser;

public interface UserService {
    //根据用户id从数据库查找用户
    public EasybuyUser selectUserById(Integer uid);
    //根据用户名查找用户信息
    public EasybuyUser selectUserByName(EasybuyUser user)throws Exception;
    //从redis中查询当前登录的用户信息
    public  EasybuyUser selectLoginFromRedis(String uuid) throws  Exception;

    //修改redis中的数据需改数据库中的数据
    public void updateUser(EasybuyUser user)throws Exception;

}
