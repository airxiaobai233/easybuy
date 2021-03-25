package com.xja.dubbo.service.impl;


import com.xja.dubbo.entity.EasybuySeckillGoods;
import com.xja.dubbo.entity.EasybuySeckillOrder;
import com.xja.dubbo.mapper.EasybuySeckillGoodsMapper;
import com.xja.dubbo.service.KillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("killGoodsService")
public class KillGoodsServiceImpl implements KillGoodsService {
    @Autowired
    private EasybuySeckillGoodsMapper easybuySeckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public EasybuySeckillGoods selectKillGoodsByGid(Integer gid) throws Exception{
        return (EasybuySeckillGoods) redisTemplate.boundHashOps("kill_goods_list").get(gid+"");
    }

    @Override
    public List<EasybuySeckillGoods> selectNowKillGoods() throws Exception{
        //首先从redis中获取所有的秒杀商品<hash>
        List<EasybuySeckillGoods> killGoodsList = redisTemplate.boundHashOps("kill_goods_list").values();
        System.out.println("redis中读取的的列表:" + killGoodsList);
        //判断redis中是否存在该秒杀列表
        if (killGoodsList==null||killGoodsList.size()==0){
            //如果不存在，则从数据库中获取该秒杀列表
            killGoodsList = easybuySeckillGoodsMapper.selectNowKillGoods();
            System.out.println("mysql中读取出来："+killGoodsList);
            //从数据库中获取以后保存到redis中
            for (EasybuySeckillGoods seckillGoods:killGoodsList){
                redisTemplate.boundHashOps("kill_goods_list").put(seckillGoods.getId()+"",seckillGoods);
            }
        }
        return killGoodsList;
    }

    @Override
    public Map<String, Object> buyKillGoods(Integer gid, Integer uid) throws Exception {
        Map<String,Object>resuMap = new HashMap<String, Object>();
        resuMap.put("status","false");//默认没有成功
        String  buykey = uid+"_"+gid;//定义用户抢购某个商品的key
        //判断该用户是否存在未支付的该商品的订单
        if (redisTemplate.boundHashOps("userKillOrder").hasKey(buykey)){
            resuMap.put("msg","您已抢购完成，需按时支付");
            return resuMap;
        }
        //还没有抢到，那么首先查询还有没有库存
        EasybuySeckillGoods killGoods = (EasybuySeckillGoods) redisTemplate.boundHashOps("kill_goods_list").get(gid + "");
        if (killGoods==null||killGoods.getStockCount()<=0){
            resuMap.put("msg","商品库存不足");
            return resuMap;
        }
        //还有库存，那么修改数量
        killGoods.setStockCount(killGoods.getStockCount()-1);
        //更新到redis中
        redisTemplate.boundHashOps("kill_goods_list").put(gid+"",killGoods);
        //如果此时数量没有了，那么结束该商品的秒杀活动
        if (killGoods.getStockCount()<=0){
            //1.更改redis中的数据
            redisTemplate.boundHashOps("kill_goods_list").delete(gid+"");
            //2.更改数据库中的数据
            easybuySeckillGoodsMapper.updateByPrimaryKeySelective(killGoods);
        }
        //生成一个秒杀订单保存到redis中
        EasybuySeckillOrder killOrder= new EasybuySeckillOrder();
        int oid = (int)(Math.random()*100)*(int)(Math.random()*100);
        killOrder.setId(Long.getLong(oid+""));
        killOrder.setCreateTime(new Date());
        killOrder.setMoney(killGoods.getCostPrice());
        killOrder.setSeckillId(Long.parseLong(gid.toString()));
        killOrder.setSellerId(killGoods.getSellerId());
        killOrder.setUserId(uid.toString());//设置用户Id
        killOrder.setStatus("0");
        //保存一下某个用户的抢购订单
        redisTemplate.boundHashOps("userKillOrder").put(buykey,killOrder);
        //因为某个用户可能抢购多个商品，所以需要把用户的所有的抢购key，单独保存
        redisTemplate.boundListOps("userkilllist_"+uid).leftPush(buykey);
        resuMap.put("msg","抢购成功");
        resuMap.put("status","true");
        System.out.println("状态"+resuMap.get("status"));
        return resuMap;
    }

    @Override
    public List<EasybuySeckillOrder> selectKillOrdersByUid(Integer uid) throws Exception {
        //获取该用户所有抢购商品的key
        List<String> keyList = redisTemplate.boundListOps("userkilllist_" + uid).range(0, -1);
        List<EasybuySeckillOrder> orderList = new ArrayList<EasybuySeckillOrder>();
        for (String key:keyList){
            EasybuySeckillOrder killOrder = (EasybuySeckillOrder) redisTemplate.boundHashOps("userKillOrder").get(key);
            orderList.add(killOrder);
        }
        return orderList;
    }


}
