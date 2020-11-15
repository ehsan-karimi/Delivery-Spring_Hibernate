package com.example.jwt.Model;

import java.sql.Timestamp;
import java.util.Date;

public class UserList<I extends Number, S, S1, S2, T extends Date, T1 extends Date> {
    private long id;
    private String username;
    private String role_name;
    private String status_name;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserList(long id, String username, String role_name, String status_name, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.username = username;
        this.role_name = role_name;
        this.status_name = status_name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole_name() {
        return role_name;
    }

    public String getStatus_name() {
        return status_name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
