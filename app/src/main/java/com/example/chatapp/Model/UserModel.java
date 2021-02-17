package com.example.chatapp.Model;

// The logical structure of a User's in the database
public class UserModel {
    private String uid,firstName,lastName,phone,bio;
    private double birthDate;
    private boolean gender;

    public UserModel() { // Constructor
        birthDate=System.currentTimeMillis(); // the current time in milliseconds
        gender=true;
    }

    // Setters and Getters
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(double birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isGender() {
        return gender;
    }
    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
