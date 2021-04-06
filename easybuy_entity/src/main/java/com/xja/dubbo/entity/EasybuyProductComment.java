package com.xja.dubbo.entity;

import java.io.Serializable;
import java.util.Date;

public class EasybuyProductComment implements Serializable {
    private Integer id;

    private Integer pid;

    private Integer uid;

    private String message;

    private Integer messagescore;

    private Integer isdelete;

    private Date createdate;

    //关联用户的昵称
    private String uname;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

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

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Integer getMessagescore() {
        return messagescore;
    }

    public void setMessagescore(Integer messagescore) {
        this.messagescore = messagescore;
    }

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }
}