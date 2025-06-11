package com.hzsca.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Animals {
    @JsonProperty("AnimalID")
    private Integer animalId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Species")
    private String species;
    @JsonProperty("Breed")
    private String breed;
    @JsonProperty("Age")
    private Float age;
    @JsonProperty("Gender")
    private String gender;
    @JsonProperty("HealthStatus")
    private String healthStatus;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("ImageURL")
    private String imageUrl;
    @JsonProperty("Source")
    private String source;

    public Animals() {
    }

    public Animals(Integer animalId, String name, String species, String breed, Float age, String gender,
                   String healthStatus, String description, String imageUrl, String source) {
        this.animalId = animalId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.healthStatus = healthStatus;
        this.description = description;
        this.imageUrl = imageUrl;
        this.source = source;
    }

    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Float getAge() {
        return age;
    }

    public void setAge(Float age) {
        this.age = age;
    }

    public String getGender() {
        if (gender != null && !gender.equals("雄性") && !gender.equals("雌性") && !gender.equals("未知")) {
            throw new IllegalArgumentException("Gender must be one of: 雄性, 雌性, 未知");
        }
        return gender;
    }

    public void setGender(String gender) {
        if (gender != null && !gender.equals("雄性") && !gender.equals("雌性") && !gender.equals("未知")) {
            throw new IllegalArgumentException("Gender must be one of: 雄性, 雌性, 未知");
        }
        this.gender = gender;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Animals{" +
                "animalId=" + animalId +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", healthStatus='" + healthStatus + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
