package com.xja.dubbo.controller;

import com.github.pagehelper.Page;
import com.xja.dubbo.entity.CarItem;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.entity.EasybuyProductCategory;
import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.service.CategoryService;
import com.xja.dubbo.service.ProductService;
import com.xja.dubbo.service.UserService;
import com.xja.dubbo.utils.CookieUtil;
import com.xja.dubbo.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    public ProductController(){
        System.out.println("控制器对象："+productService);
    }
    @RequestMapping("list")
    public String list(Integer npage,EasybuyProduct product , Model model,@CookieValue(required = false) String  uuid,HttpServletRequest request){
        try {
            System.out.println("调用了业务:"+productService);
            if (npage==null)
                npage=1;
            //获取登录用户的信息
            EasybuyUser loginuser = userService.selectLoginFromRedis(uuid);
            if (loginuser!=null){
                List carItemList = redisTemplate.boundHashOps("user_car_"+loginuser.getId()).values();
                model.addAttribute("carItemList",carItemList);
            }

            //获取所有的分类
            List<EasybuyProductCategory> categoryList = categoryService.selectFirstCategory();
            //处理价格范围的区间：1：0-100，2：100-200；3：200以上
            if (product!=null&&product.getPrice()!=null&&product.getPrice()==1.0F){
                System.out.println("价格区间:1");
                product.setMaxPrice(100.0f);
                product.setMinPrice(0f);
            }
            if (product!=null&&product.getPrice()!=null&&product.getPrice()==2.0F){
                System.out.println("价格区间:2");
                product.setMaxPrice(200.0f);
                product.setMinPrice(100f);
            }
            if (product!=null&&product.getPrice()!=null&&product.getPrice()==3.0F){
                System.out.println("价格区间:3");
                product.setMinPrice(200f);
            }

            Map<String, Object> productPage = new HashMap<String, Object>();
            productPage.put("npage",npage);
            if (product!=null &&product.getKeyword()!=null&&!"".equals(product.getKeyword())){
                System.out.println("存在条件："+product.getKeyword());
                productPage=productService.selectProdsBySearch(npage,product);
            }else {
                productPage = productService.selectProds(npage,product);
            }

            model.addAttribute("npage",npage);
            model.addAttribute("pageCount",productPage.get("pageCount"));
            model.addAttribute("productCount",productPage.get("productCount"));
            model.addAttribute("productList",productPage.get("productList"));
            model.addAttribute("product",product);  //保存搜索的条件

            //查询redis中保存的用户
            System.out.println("uuid:"+uuid);
            System.out.println("登录用户的信息:"+ loginuser.toString());
            model.addAttribute("loginuser",loginuser);
            //添加一级分类
            model.addAttribute("categoryList",categoryList);


            System.out.println("执行该业务结束！！");
            return "/product_list";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/error.jsp";
    }

    @RequestMapping("/{pid}/addcar")
    public String addcar(@PathVariable("pid") Integer pid, HttpServletRequest request, HttpServletResponse response,Model model,@CookieValue(required = false ) String uuid){
        try{
            //获取登录用户的信息
            EasybuyUser loginuser = userService.selectLoginFromRedis(uuid);
            //获取购物商品的信息
            EasybuyProduct easybuyProduct = productService.selectById(pid);
            List<CarItem> carItemList = null;
            //判断用户是否登陆
            if (loginuser ==null){
                //需要把购物车的信息保存到cookie中
                //首先根据获取 cookie中的购物车的信息，判断该商品是否已被购买过，没有就新建有就+1
                String userCarList = CookieUtil.getCookieValue(request, "user_car_list", true);
                if (userCarList!=null&&!"".equals(userCarList)){
                    carItemList = JsonUtil.jsonToList(userCarList,CarItem.class);
                }else {
                    carItemList = new ArrayList<CarItem>();
                }
                //判断购物车中的商品是否存在
                boolean isHave = false;
                for (CarItem carItem:carItemList){
                    //存在
                    if (carItem.getProduct().getId()==pid){
                        carItem.setBuyNum(carItem.getBuyNum()+1);
                        isHave=true;
                        break;
                    }
                }
                //不存在
                if (isHave==false){
                    CarItem carItem = new CarItem(easybuyProduct,1);
                    carItemList.add(carItem);
                }

                System.out.println("这些数据保存到了cookiecarItemList:"+carItemList);
                //把购物信息存放在cookie中
                CookieUtil.setCookie(request,response,"user_car_list",JsonUtil.objectToJson(carItemList),true);
            }else {
                //保存商品到redis中，与用户对应
                productService.addCarToRedis(loginuser,easybuyProduct,1);
            }
            return "redirect:/product/list";
        }catch (Exception e){
            e.printStackTrace();
        }
        return  "redirect:/error.jsp" ;
    }








}
