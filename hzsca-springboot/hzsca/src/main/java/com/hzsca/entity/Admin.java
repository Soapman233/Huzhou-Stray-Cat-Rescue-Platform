package com.hzsca.entity;

public class Admin extends Account {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String name;
    public Admin(Integer id, String username, String password, String role, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    public Admin(Integer id, String username, String password, String role, String name, String newPassword, String avatar, Integer id1, String username1, String password1, String role1, String name1) {
        super(id, username, password, role, name, newPassword, avatar);
        this.id = id1;
        this.username = username1;
        this.password = password1;
        this.role = role1;
        this.name = name1;
    }
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
