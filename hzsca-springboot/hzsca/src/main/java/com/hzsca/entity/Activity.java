package com.hzsca.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Activity {
    private Integer id;
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime date;

    private String Location;  // 保持与数据库字段大写一致
    private String imgurl;
    private String detail;
    private String status;

    // Getters
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLocation() {
        return Location;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getDetail() {
        return detail;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
