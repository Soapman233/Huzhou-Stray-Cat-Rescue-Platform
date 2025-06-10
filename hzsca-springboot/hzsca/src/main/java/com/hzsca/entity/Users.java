package com.hzsca.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

public class Users {
    @JsonProperty("UserID")
    private Integer UserID;
    @JsonProperty("Username")
    @JsonAlias({"username"})
    private String username;
    @JsonProperty("Password")
    @JsonAlias({"password"})
    private String password;
    @JsonProperty("Phone")
    @JsonAlias({"phone"})
    private String phone;
    @JsonProperty("Name")
    @JsonAlias({"name"})
    private String name;
    @JsonProperty("Gender")
    @JsonAlias({"gender"})
    private Integer gender;
    @JsonProperty("Profile")
    @JsonAlias({"profile"})
    private String profile;
    @JsonProperty("Role")
    @JsonAlias({"role"})
    private String role;
    @JsonProperty("Avatar")
    @JsonAlias({"avatar"})
    private String avatar;

    public Users() {
    }

    public Users(Integer UserID, String username, String password, String phone, String name, Integer gender, String profile, String role, String avatar) {
        this.UserID = UserID;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.profile = profile;
        this.role = role;
        this.avatar = avatar;
    }

    public Integer getUserID() {
        return UserID;
    }

    public void setUserID(Integer UserID) {
        this.UserID = UserID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
