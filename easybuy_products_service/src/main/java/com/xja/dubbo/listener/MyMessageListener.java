package com.xja.dubbo.listener;

import com.xja.dubbo.entity.EasybuyOrder;
import com.xja.dubbo.entity.EasybuyOrderDetail;
import com.xja.dubbo.entity.EasybuyProduct;
import com.xja.dubbo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashMap;
import java.util.List;

public class MyMessageListener implements MessageListener {
    @Autowired
    private ProductService productService;

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage)message;
            HashMap<String,Object> messageMap =  (HashMap<String,Object>)objectMessage.getObject();
            EasybuyOrder order = (EasybuyOrder)messageMap.get("order");
            List<EasybuyOrderDetail> detailList =(List<EasybuyOrderDetail>)messageMap.get("orderDetailList");
            System.out.println("order:"+order);
            System.out.println("detailList:"+detailList);

            //修改商品的库存数量
            for(EasybuyOrderDetail orderDetail : detailList){
                EasybuyProduct product = productService.selectById(orderDetail.getProductid());
                product.setStock(product.getStock()-orderDetail.getQuantity() );
                productService.updateProduct(product);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
