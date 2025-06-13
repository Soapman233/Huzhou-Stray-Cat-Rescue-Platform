package com.hzsca.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Activity {
    private Integer id;
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime activityTime;

    private String status;
    private String location;
    private String imgurl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime date;

    private String detail;

    // Getters
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getActivityTime() {
        return activityTime;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getImgurl() {
        return imgurl;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDetail() {
        return detail;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setActivityTime(LocalDateTime activityTime) {
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

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
