package com.example.fruitvegetablestall;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String email;
    private String gender;
    private String password;

    public User(String username, String email, String gender, String password) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
public String toString() {
    return "User{username='" + username + "', email='" + email + "', gender='" + gender + "'}";
}

}
