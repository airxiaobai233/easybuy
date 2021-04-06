import com.xja.dubbo.entity.EasybuyUser;
import com.xja.dubbo.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class Userstest {
    public static void main(String[] args) {
        try {
            System.out.println("启动了user业务的服务");
            ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext-service.xml","applicationContext_redistemplate.xml","applicationContext-jms-consumer.xml");
            UserService userService = (UserService) ac.getBean("userService");
            EasybuyUser user = userService.selectUserById(2);
            System.out.println("登录者:" +user.getLoginname());
            //线程阻塞
            Scanner input = new Scanner(System.in);
            input.nextInt();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
