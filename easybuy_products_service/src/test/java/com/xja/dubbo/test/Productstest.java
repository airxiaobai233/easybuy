package com.xja.dubbo.test;


import com.github.pagehelper.Page;
import com.xja.dubbo.entity.EasybuyNews;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.entity.EasybuyProductCategory;
import com.xja.dubbo.service.CategoryService;
import com.xja.dubbo.service.NewsService;
import com.xja.dubbo.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Productstest {
    public static void main(String[] args) {
        try {
            System.out.println("启动了productService业务的服务");
            ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext-service.xml","applicationContext_redistemplate.xml","applicationContext-solr.xml");
            ProductService productService = (ProductService) ac.getBean("productService");

            /*Map<String,Object> pmap = new HashMap<String, Object>();
            pmap.put("npage",1);
            Page<EasybuyProduct> productPage = productService.selectProds(pmap);
            System.out.println(productPage.getPages());*/
//            EasybuyProduct easybuyProduct = productService.selectById(733);
//            System.out.println(easybuyProduct.getName());
            /*Query query = new SimpleQuery("*:*");
            SolrTemplate solrTemplate = (SolrTemplate) ac.getBean("solrTemplate");
            solrTemplate.delete(query);
            solrTemplate.commit();
            System.out.println("haole!");*/
            /*EasybuyProduct product = new EasybuyProduct();
            Map<String, Object> map = productService.selectProds(1,product);
            System.out.println("页数："+map.get("pageCount"));
            Map<String, Object> map1 = productService.selectProds(1,product);
            List<EasybuyProduct> productList = (List<EasybuyProduct>) map1.get("productList");
            for (EasybuyProduct pro:productList) {
                System.out.println(pro.getName());
            }*/
            CategoryService categoryService = (CategoryService) ac.getBean("categoryService");
            List<EasybuyProductCategory> categoryList = categoryService.selectFirstCategory();
            for (EasybuyProductCategory category:categoryList){
                System.out.println(category.getName());
            }
            //线程阻塞
            Scanner input = new Scanner(System.in);
            input.nextInt();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
