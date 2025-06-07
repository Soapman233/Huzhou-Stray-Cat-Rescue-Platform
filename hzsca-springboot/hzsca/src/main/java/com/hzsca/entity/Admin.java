package com.hzsca.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends Account {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String name;

}
