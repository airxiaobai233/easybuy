package com.xja.dubbo.test;


import com.github.pagehelper.Page;
import com.xja.dubbo.entity.EasybuyNews;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.service.NewsService;
import com.xja.dubbo.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Productstest {
    public static void main(String[] args) {
        try {
            System.out.println("启动了productService业务的服务");
            ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext-service.xml","applicationContext_redistemplate.xml");
            ProductService productService = (ProductService) ac.getBean("productService");

            /*Map<String,Object> pmap = new HashMap<String, Object>();
            pmap.put("npage",1);
            Page<EasybuyProduct> productPage = productService.selectProds(pmap);
            System.out.println(productPage.getPages());*/
            EasybuyProduct easybuyProduct = productService.selectById(733);
            System.out.println(easybuyProduct.getName());


            //线程阻塞
            Scanner input = new Scanner(System.in);
            input.nextInt();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
