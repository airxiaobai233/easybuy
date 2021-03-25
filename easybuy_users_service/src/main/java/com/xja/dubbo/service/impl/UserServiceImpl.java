package com.xja.dubbo.service.impl;


import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.mapper.EasybuyUserMapper;
import com.xja.dubbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    EasybuyUserMapper easybuyUserMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public UserServiceImpl(){
        System.out.println("创建了用户的业务对象");
    }

    @Override
    public EasybuyUser selectUserById(Integer uid) {
        return easybuyUserMapper.selectByPrimaryKey(uid);
    }

    @Override
    public EasybuyUser selectUserByName(EasybuyUser user)throws Exception {
        return easybuyUserMapper.selectUserByName(user.getLoginname());
    }
    @Override
    //从redis中查询当前登录的用户信息
    public  EasybuyUser selectLoginFromRedis(String uuid) throws  Exception{
        EasybuyUser easybuyUser = (EasybuyUser) redisTemplate.boundValueOps("loginuser_" + uuid).get();
        System.out.println(easybuyUser.getLoginname());
        return easybuyUser;
    }

}
