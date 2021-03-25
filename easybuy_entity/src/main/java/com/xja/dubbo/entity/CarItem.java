package com.xja.dubbo.entity;

import java.io.Serializable;

public class CarItem implements Serializable {
    private EasybuyProduct product;
    private Integer buyNum;

    public CarItem() {
    }

    public CarItem(EasybuyProduct product, Integer buyNum) {
        this.product = product;
        this.buyNum = buyNum;
    }

    public EasybuyProduct getProduct() {
        return product;
    }

    public void setProduct(EasybuyProduct product) {
        this.product = product;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }
}
