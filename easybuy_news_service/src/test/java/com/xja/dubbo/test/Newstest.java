package com.xja.dubbo.test;


import com.github.pagehelper.Page;
import com.xja.dubbo.entity.EasybuyNews;
import com.xja.dubbo.service.NewsService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class Newstest {
    public static void main(String[] args) {
        try {
            System.out.println("启动了News业务的服务");
            ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext-service.xml");
            NewsService newsService = (NewsService) ac.getBean("newsService");
            Page<EasybuyNews> newsPage = newsService.selectNews();
            System.out.println("新闻的条数:" +newsPage.getPages());
            //线程阻塞
            Scanner input = new Scanner(System.in);
            input.nextInt();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
