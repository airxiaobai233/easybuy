package com.xja.dubbo.service.impl;


import com.xja.dubbo.entity.EasybuySeckillGoods;
import com.xja.dubbo.entity.EasybuySeckillOrder;
import com.xja.dubbo.mapper.EasybuySeckillGoodsMapper;
import com.xja.dubbo.mapper.EasybuySeckillOrderMapper;
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
    private EasybuySeckillOrderMapper easybuySeckillOrderMapper;

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

    @Override
    public void addPayOrder(EasybuySeckillOrder pkillorder) throws Exception {
        //从redis中获取抢购的订单对象
        String buykey = pkillorder.getUserId()+"_"+pkillorder.getSeckillId();
        //获取订单对象
        EasybuySeckillOrder killOrder =(EasybuySeckillOrder) redisTemplate.boundHashOps("userKillOrder").get(buykey);
        //把该订单的状态改为支付，同时完成订单对象的添加
        killOrder.setId(pkillorder.getId());
        killOrder.setReceiver(pkillorder.getReceiver());
        killOrder.setReceiverAddress(pkillorder.getReceiverAddress());
        killOrder.setReceiverMobile(pkillorder.getReceiverMobile());
        killOrder.setStatus("3");  //已支付
        easybuySeckillOrderMapper.insertSelective(killOrder);

        //抢购订单一旦完成，需要把该订单从redis中删除，同时删除用户的订单对应的KEY
        redisTemplate.boundHashOps("userKillOrder").delete(buykey);
        redisTemplate.boundListOps("userkilllist_"+pkillorder.getUserId()).remove(0,buykey);

    }

    //超时订单处理
    @Override
    public void deleteUserKillOrder(Integer gid, Integer uid) throws Exception {
        String buykey = uid +"_"+gid;
        //获取该订单的信息
        EasybuySeckillOrder killOrder = (EasybuySeckillOrder) redisTemplate.boundHashOps("userKillOrder").get(buykey);
        if (killOrder==null)
            return;
        //获取抢购的商品信息
        EasybuySeckillGoods goods = (EasybuySeckillGoods) redisTemplate.boundHashOps("kill_goods_list").get(gid.toString());
        if (goods!=null){
            //把抢购的商品的数量增加1
            goods.setStockCount(goods.getStockCount()+1);
            redisTemplate.boundHashOps("kill_goods_list").put(gid.toString(),goods);
        }else {
            //此时如果抢购的商品不存在了，说明抢购完毕，需要从mysql中恢复一件商品到redis中
            goods = easybuySeckillGoodsMapper.selectByPrimaryKey(Long.parseLong(gid.toString()));
            goods.setStockCount(goods.getStockCount()+1);
            easybuySeckillGoodsMapper.updateByPrimaryKeySelective(goods);
            redisTemplate.boundHashOps("kill_goods_list").put(gid.toString(),goods);
        }
        //把该订单删除
        redisTemplate.boundHashOps("userKillOrder").delete(buykey);
        //把该用户的订单的key列表删除
        redisTemplate.boundListOps("userkilllist_"+uid).remove(0,buykey);

    }

    @Override
    public EasybuySeckillOrder selectKillOrderByUiuGid(Integer uid,Integer gid) throws Exception{
        String buykey = uid+"_"+gid;
        return  (EasybuySeckillOrder) redisTemplate.boundHashOps("userKillOrder").get(buykey);
    }

    @Override
    public EasybuySeckillOrder selectKillOrderByOid(String oid) throws Exception {
        return easybuySeckillOrderMapper.selectByPrimaryKey(Long.parseLong(oid));    }

    @Override
    public void updateKillOrderByOid(EasybuySeckillOrder order) throws Exception {
        easybuySeckillOrderMapper.updateByPrimaryKeySelective(order);
    }


    //测试有没有用easybuySeckillGoodsMapper
    @Override
    public void test() {
        EasybuySeckillGoods easybuySeckillGoods = easybuySeckillGoodsMapper.selectByPrimaryKey(1L);
        System.out.println(easybuySeckillGoods.getTitle());
    }


}
