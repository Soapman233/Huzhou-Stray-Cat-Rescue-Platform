package com.hzsca.entity;

import lombok.Getter;

import java.util.Date;

@Getter
public class Activity {
    // Getters and Setters
    private Integer id;
    private String title;
    private String content;
    private Date activityTime;
    private String status;
    private String location;
    private String imgurl;
    private Date date;
    private String detail;
    
    public void setId(Long id) {
        this.id = id.intValue();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
}
