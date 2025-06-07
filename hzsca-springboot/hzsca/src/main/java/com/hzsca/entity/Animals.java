package com.hzsca.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Animals {
    private Integer AnimalId;
    private String Name;
    private String Species;
    private String Breed;
    private Integer Age;
    private String Gender;
    private String HealthStatus;
    private String Description;
    private String ImageURL;
    private String Source;
}
