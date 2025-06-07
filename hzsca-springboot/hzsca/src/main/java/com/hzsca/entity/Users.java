package com.hzsca.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users extends Account{
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String name;
    private Integer sex;
    private String avatar;
    private String description;

}
