package com.xja.dubbo.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xja.dubbo.entity.CarItem;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.entity.EasybuyProductCategory;
import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.mapper.EasybuyProductCategoryMapper;
import com.xja.dubbo.mapper.EasybuyProductMapper;
import com.xja.dubbo.service.ProductService;
import com.xja.dubbo.utils.PageSizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    EasybuyProductMapper easybuyProductMapper;


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public Map<String,Object> selectProds(Integer npage,EasybuyProduct product) throws Exception {
        //设置分页查询条件
        if (npage==null)
            npage=1;

        //把数据源转移到solr中
        Query query = new SimpleQuery("*:*");
        //判断其他条件
        if(product.getMinPrice()!=null){
            Criteria criteria = Criteria.where("prod_price").greaterThan(product.getMinPrice());
            query.addCriteria(criteria);
        }
        if(product.getMaxPrice()!=null){
            Criteria criteria = Criteria.where("prod_price").lessThan(product.getMaxPrice());
            query.addCriteria(criteria);
        }
        if (product.getCategorylevel1id()!=null){
            Criteria criteria = Criteria.where("prod_firstLevelId").is(product.getCategorylevel1id());
            query.addCriteria(criteria);
        }

        query.setOffset((npage-1)*PageSizeUtil.PRODUCT_PAGE_SIZE);
        query.setRows(PageSizeUtil.PRODUCT_PAGE_SIZE);
        ScoredPage<EasybuyProduct> productScoredPage  = solrTemplate.queryForPage(query, EasybuyProduct.class);
        //第一次查solr中没有数据，因此需要从数据库中进行查询
        if (productScoredPage==null || productScoredPage.getContent().size()==0){
            //从数据库中查询数据，保存到solr中
            List<EasybuyProduct> productList = easybuyProductMapper.selectProds();
            solrTemplate.saveBeans(productList);
            solrTemplate.commit();
            productScoredPage  = solrTemplate.queryForPage(query, EasybuyProduct.class);
        }

        Map<String,Object> resuMap = new HashMap<String,Object>();
        resuMap.put("pageCount",productScoredPage.getTotalPages());
        resuMap.put("productList",productScoredPage.getContent());
        resuMap.put("productCount",productScoredPage.getTotalElements());
        return resuMap;
    }

    //将购物车信息添加到redis中
    @Override
    public void addCarToRedis(EasybuyUser loginuser, EasybuyProduct buyProd, int buyNum) throws Exception {
        //从redis中获取该用户的购物车列表信息
        CarItem carItem = null;
        if(redisTemplate.boundHashOps("user_car_"+loginuser.getId()).hasKey(buyProd.getId()+"")){
            carItem = (CarItem) redisTemplate.boundHashOps("user_car_" + loginuser.getId()).get(buyProd.getId() + "");
            carItem.setBuyNum(carItem.getBuyNum()+1);
        }else {
            carItem = new CarItem(buyProd,buyNum);
        }
        redisTemplate.boundHashOps("user_car_"+loginuser.getId()).put( buyProd.getId()+"",carItem);
        System.out.println("把购物车的数据保存到redis中");
    }

    //从redis中获取某个用户的购物车信息
    @Override
    public List<CarItem> selectRedisProductAll(EasybuyUser loginuser) throws Exception {
        return (List<CarItem>)redisTemplate.boundHashOps("user_car_"+loginuser.getId()).values();
    }

    //修改商品库存信息\
    @Override
    public void updateProduct(EasybuyProduct product) throws Exception {
        //修改商品的时候，应该保证redis和mysql中的数据同步
        easybuyProductMapper.updateByPrimaryKey(product);
        //替换redis中的数据
        solrTemplate.saveBean(product);
        solrTemplate.commit();
    }

    @Override
    public EasybuyProduct selectById(Integer pid) throws Exception {
        return easybuyProductMapper.selectByPrimaryKey(pid);
    }

    @Override
    public Map<String, Object> selectProdsBySearch(Integer npage, EasybuyProduct product) throws Exception {
        //需要把数据源从数据库中转移到solr中，保证数据加载
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的参数
        HighlightOptions options = new HighlightOptions();
        //设置高亮字段和高亮样式
        options.addField("prod_name","prod_desc","prod_level1Name");
        options.setSimplePrefix("<span style='color:red '>");
        options.setSimplePostfix("</span>");
        //查询条件和高亮参数绑定
        query.setHighlightOptions(options);


        //设定查询条件
        Criteria criteria = Criteria.where("prod_keywords").is(product.getKeyword());
        query.addCriteria(criteria);
        //判断其他条件
        if(product.getMinPrice()!=null){
            criteria = Criteria.where("prod_price").greaterThan(product.getMinPrice());
            query.addCriteria(criteria);
        }
        if(product.getMaxPrice()!=null){
            criteria = Criteria.where("prod_price").lessThan(product.getMaxPrice());
            query.addCriteria(criteria);
        }
        if (product.getCategorylevel1id()!=null){
            criteria = Criteria.where("prod_firstLevelId").is(product.getCategorylevel1id());
            query.addCriteria(criteria);
        }

        //设置分页的方法
        query.setOffset((npage-1)*PageSizeUtil.PRODUCT_PAGE_SIZE);
        query.setRows(PageSizeUtil.PRODUCT_PAGE_SIZE);


        //调用分页的方法
        HighlightPage<EasybuyProduct> productHighlightPage = solrTemplate.queryForHighlightPage(query, EasybuyProduct.class);
        //设置高亮数据
        List<HighlightEntry<EasybuyProduct>> highlightEntryList = productHighlightPage.getHighlighted();
        for (HighlightEntry<EasybuyProduct> highlightEntry:highlightEntryList){
            //获取查询的结果对象
            EasybuyProduct resuproduct = highlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();

            //遍历每一个高亮数据，这个数据就是拆出来的那些词
            for (HighlightEntry.Highlight highlight:highlightList){
                //获取高亮的字段名称
                String fieldname = highlight.getField().getName();
                //判断
                if (fieldname.equals("prod_name")&&highlight.getSnipplets().size()!=0){
                    resuproduct.setName(highlight.getSnipplets().get(0));
                }
                if(fieldname.equals("prod_desc") && highlight.getSnipplets().size() !=0){
                    resuproduct.setDescription(   highlight.getSnipplets().get(0));
                }
                if(fieldname.equals("prod_level1Name") && highlight.getSnipplets().size() !=0){
                    resuproduct.setLevel1Name(highlight.getSnipplets().get(0));
                }
                System.out.println("高亮后的结果："+resuproduct.getName()+"\t"+resuproduct.getLevel1Name());
            }
        }
        Map<String,Object> resuMap = new HashMap<String, Object>();
        resuMap.put("pageCount",productHighlightPage.getTotalPages());
        resuMap.put("productList",productHighlightPage.getContent());
        resuMap.put("productCount",productHighlightPage.getTotalElements());
        return resuMap;
    }

    @Override
    public List<EasybuyProduct> selectProducts() throws Exception {
        return easybuyProductMapper.selectProds();
    }



}
