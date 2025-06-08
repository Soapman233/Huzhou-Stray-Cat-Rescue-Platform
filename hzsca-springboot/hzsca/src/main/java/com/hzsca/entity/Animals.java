package com.hzsca.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Animals {
    @JsonProperty("AnimalID")
    private Integer AnimalID;
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Species")
    private String Species;
    @JsonProperty("Breed")
    private String Breed;
    @JsonProperty("Age")
    private Integer Age;
    @JsonProperty("Gender")
    private String Gender;
    @JsonProperty("HealthStatus")
    private String HealthStatus;
    @JsonProperty("Description")
    private String Description;
    @JsonProperty("ImageURL")
    private String ImageURL;
    @JsonProperty("Source")
    private String Source;

    public Animals() {
    }

    public Animals(Integer AnimalID, String Name, String Species, String Breed, Integer Age, String Gender,
                   String HealthStatus, String Description, String ImageURL, String Source) {
        this.AnimalID = AnimalID;
        this.Name = Name;
        this.Species = Species;
        this.Breed = Breed;
        this.Age = Age;
        this.Gender = Gender;
        this.HealthStatus = HealthStatus;
        this.Description = Description;
        this.ImageURL = ImageURL;
        this.Source = Source;
    }

    public Integer getAnimalID() {
        return AnimalID;
    }

    public void setAnimalID(Integer AnimalID) {
        this.AnimalID = AnimalID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSpecies() {
        return Species;
    }

    public void setSpecies(String Species) {
        this.Species = Species;
    }

    public String getBreed() {
        return Breed;
    }

    public void setBreed(String Breed) {
        this.Breed = Breed;
    }

    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer Age) {
        this.Age = Age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getHealthStatus() {
        return HealthStatus;
    }

    public void setHealthStatus(String HealthStatus) {
        this.HealthStatus = HealthStatus;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    @Override
    public String toString() {
        return "Animals{" +
                "AnimalID=" + AnimalID +
                ", Name='" + Name + '\'' +
                ", Species='" + Species + '\'' +
                ", Breed='" + Breed + '\'' +
                ", Age=" + Age +
                ", Gender='" + Gender + '\'' +
                ", HealthStatus='" + HealthStatus + '\'' +
                ", Description='" + Description + '\'' +
                ", ImageURL='" + ImageURL + '\'' +
                ", Source='" + Source + '\'' +
                '}';
    }
}
