package com.xja.dubbo.entity;

import java.io.Serializable;

public class EasybuyProductImages implements Serializable {
    private Integer id;

    private Integer pid;

    private String filedesc;

    private String filename;

    private Integer isdelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getFiledesc() {
        return filedesc;
    }

    public void setFiledesc(String filedesc) {
        this.filedesc = filedesc == null ? null : filedesc.trim();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }
}