package com.example.jwt.Model.User;

public class UserUpdateResponse {
    private Boolean successfully;
    private String token;

    public Boolean getSuccessfully() {
        return successfully;
    }

    public void setSuccessfully(Boolean successfully) {
        this.successfully = successfully;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
