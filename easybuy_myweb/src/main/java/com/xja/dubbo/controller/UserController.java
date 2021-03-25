package com.xja.dubbo.controller;

import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.service.UserService;
import com.xja.dubbo.utils.CookieUtil;
import com.xja.dubbo.utils.JsonUtil;
import com.xja.dubbo.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("login")
    public String login(EasybuyUser user, HttpServletRequest request, HttpServletResponse response, Model model){
        try{
            System.out.println("进来了");
            EasybuyUser loginAdmin = userService.selectUserByName(user);
            if(loginAdmin==null){
                model.addAttribute("msg", "名称错误");
                return "/login.jsp";
            }
            //判断密码是否正确
            if(!loginAdmin.getPassword().equals(loginAdmin.getPassword())){
                model.addAttribute("msg", "密码错误");
                return "/login.jsp";
            }
            //如果登录成功，需要把用户信息保存到redis中
            //为了区分不用的登录，需要为每一个用户生成一个标识符，标识符保存cookie中
            String uuid = UuidUtil.getUUID();
            redisTemplate.boundValueOps("loginuser_"+uuid).set(loginAdmin);
            System.out.println("登录成功！！！");
            //把用户的标识符，保存到cookie中
            CookieUtil.setCookie(request,response,"uuid",uuid);
            return "redirect:/product/list";
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "redirect:/error.jsp";
    }
}
