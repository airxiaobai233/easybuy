package com.xja.dubbo.controller;

import com.xja.dubbo.entity.EasybuySeckillGoods;
import com.xja.dubbo.entity.EasybuySeckillOrder;
import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.service.KillGoodsService;
import com.xja.dubbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("killgoods")
public class KillGoodsController {
    @Autowired
    private KillGoodsService killGoodsService;
    @Autowired
    private UserService userService;

    @RequestMapping("list")
    public String list(Model model){
        try {
            List<EasybuySeckillGoods> seckillGoodsList = killGoodsService.selectNowKillGoods();
            model.addAttribute("seckillGoodsList",seckillGoodsList);
            return "/killgoods_list";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/error.jsp";
    }

    @RequestMapping("/{gid}/detail")
    public String detail(@PathVariable("gid")Integer gid,Model model){
        try {
            System.out.println("gid:"+gid);
            EasybuySeckillGoods seckillGoods = killGoodsService.selectKillGoodsByGid(gid);
            //计算剩余的倒计时
            long seconds = (seckillGoods.getEndTime().getTime() - new Date().getTime()) / 1000;
            model.addAttribute("killGood",seckillGoods);
            model.addAttribute("seconds",seconds);
            return  "/killgoods_detail";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/error.jsp";
    }

    @RequestMapping("/{gid}/addcar")
    public String addcar(@PathVariable("gid") Integer gid, @CookieValue(required = false)String uuid, HttpSession session){
        try{
            //1.检查这个用户有没有登录
            EasybuyUser loginuser = userService.selectLoginFromRedis(uuid);
            if (loginuser==null){
                session.setAttribute("msg","没有登录");
                return  "redirect:/login.jsp";
            }
            Map<String, Object> resuMap = killGoodsService.buyKillGoods(gid,loginuser.getId());
            session.setAttribute("status",resuMap.get("status"));
            session.setAttribute("msg",resuMap.get("msg"));
            return  "redirect:/killgoods/showcar";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/error.jsp";
    }

    //展示抢购订单
    @RequestMapping("/showcar")
    public String showcar(@CookieValue(required = false)String uuid,Model model){
        try {
            EasybuyUser loginuser = userService.selectLoginFromRedis(uuid);
            if (loginuser==null){
                return  "redirect:/login.jsp";
            }
            //获取该用户的所有的抢购数据列表
            List<EasybuySeckillOrder> seckillOrderList = killGoodsService.selectKillOrdersByUid(loginuser.getId());
            model.addAttribute("seckillOrderList",seckillOrderList);
            return  "/killorder_list";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/error.jsp";
    }
}
