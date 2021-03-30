import com.xja.dubbo.entity.EasybuySeckillGoods;
import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.service.KillGoodsService;
import com.xja.dubbo.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Scanner;

public class KillGoodstest {
    public static void main(String[] args) {
        try {
            System.out.println("启动了killgoods业务的服务");
            ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext-service.xml","applicationContext_redistemplate.xml","applicationContext-solr.xml");
            KillGoodsService killGoodsService = (KillGoodsService) ac.getBean("killGoodsService");
            /*List<EasybuySeckillGoods> easybuySeckillGoods = killGoodsService.selectNowKillGoods();
            System.out.println("查找到了："+easybuySeckillGoods);
            EasybuySeckillGoods easybuySeckillGoods1 = killGoodsService.selectKillGoodsByGid(1);
            System.out.println("打印一下："+easybuySeckillGoods1.getTitle());*/
            killGoodsService.test();
            //线程阻塞
            Scanner input = new Scanner(System.in);
            input.nextInt();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
