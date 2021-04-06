package com.xja.dubbo.controller;

import com.github.pagehelper.Page;
import com.xja.dubbo.entity.*;
import com.xja.dubbo.service.*;
import com.xja.dubbo.utils.CookieUtil;
import com.xja.dubbo.utils.JsonUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.*;

@Controller
@RequestMapping("product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImagesService productImagesService;
    @Autowired
    private ProductCommentService productCommentService;

    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private FreeMarkerConfigurer configurer;

    public ProductController(){
        System.out.println("控制器对象："+productService);
    }

    @RequestMapping("changePage")
    @ResponseBody
    public Map changePage(Integer pid,Integer npage){

        System.out.println("开始查询换页的数据了");
        if (npage==null||npage.equals("")||npage<1){
            npage=1;
        }

        //获取图书的评论
        Map<String,Object> map = new HashMap<String, Object>();
        //加载评论列表
        Map<String, Object> commMap = null;
        try {
            commMap = productCommentService.selectCommentByPid(pid, npage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put("commMap",commMap);
        map.put("npage",npage);
        System.out.println("换页结束！！！");
        return map;

    }

    @RequestMapping("/{pid}/detail")
    public String detail(@PathVariable("pid")Integer pid,Model model,HttpServletRequest request){
        try {
            String serverPath = request.getServletContext().getRealPath("/");
            System.out.println("serverPath:"+serverPath);
            String fileName = serverPath+"product_"+pid+".html";
            //目标产生一个静态页面（如果存在该页面，则直接返回不存在就生成）
            File file = new File(fileName);

            //实际应用的时候要加上这一段
            if (file.exists()){
                file.delete();
//                return "redirect:/product_"+pid+".html";
            }
            //首先加载一个商品对象
            EasybuyProduct product = productService.selectById(pid);
            //加载商品对应的图片内容
            List<EasybuyProductImages> imagesList = productImagesService.selectImagesByPid(pid);

            //加载评论列表
            Map<String, Object> commMap = productCommentService.selectCommentByPid(pid, 1);
            
            //获取各级评论的数量
            Map<String, Object> commLevelNumMap = productCommentService.selectLevelCommentNumByPid(pid);
            //计算好评的百分比
            double zts = Double.parseDouble(commLevelNumMap.get("zts").toString());
            double hps = Double.parseDouble(commLevelNumMap.get("hps").toString());
            int hpb = (int)(hps/zts*100);
            System.out.println("总评数："+zts+",好评数:"+hps+",好评比:"+hpb);
            commLevelNumMap.put("hpb",hpb);

            System.out.println("=============================="+commMap);
            System.out.println("评论列表:"+commMap.get("commentList"));
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("product",product);
            map.put("npage",1);
            map.put("imagesList",imagesList);
            map.put("commMap",commMap);
            map.put("commLevelNumMap",commLevelNumMap);
            //生成页面的内容
            Configuration configuration = configurer.getConfiguration();
            //读取模板的内容
            Template template = configuration.getTemplate("Product.ftl");
            //输出文件
            File targetFile = new File(fileName);
            FileWriter writer = new FileWriter(targetFile);
//            template.process(map,writer);
            template.process(map,new OutputStreamWriter(new FileOutputStream(targetFile),"utf-8"));
            //关闭流
            writer.close();
            return "redirect:/product_"+pid+".html";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/error.jsp";
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

    @RequestMapping("/showCar")
    public String showCar(Model model, HttpServletRequest request, HttpServletResponse response,
                          @CookieValue(required=false) String uuid){
        try {
            //获取登录用户的信息
            EasybuyUser loginuser = userService.selectLoginFromRedis(uuid);
            //获取购物车的信息
            //获得cookies的数据
            String userCarList = CookieUtil.getCookieValue(request, "user_car_list", true);
            System.out.println(userCarList);
            List<CarItem> carItemList =null;
            if(userCarList ==null || "".equals(userCarList)){
                carItemList =new ArrayList<>();
            }else{

                carItemList = JsonUtil.jsonToList(userCarList,CarItem.class);
            }

            if(loginuser !=null){
                for (CarItem carItem :carItemList){
                    EasybuyProduct product = carItem.getProduct();
                    productService.addCarToRedis(loginuser,product,carItem.getBuyNum());
                }
                //删除 Cookies中的数据
                CookieUtil.deleteCookie(request,response,"user_car_list");
                carItemList = productService.selectRedisProductAll(loginuser);
            }
            model.addAttribute("carItemList",carItemList);
            System.out.println(carItemList);
            return "BuyCar";
        }catch (Exception e){
            e.printStackTrace();
        }

        return "error";
    }








}
