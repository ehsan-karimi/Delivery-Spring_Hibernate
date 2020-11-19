package com.example.jwt.Model.Product;

import com.example.jwt.Model.StatusDao;
import com.example.jwt.Model.TagDao;
import com.example.jwt.Model.User.UserDao;

import java.sql.Timestamp;
import java.util.Date;

public class ProductList<L extends Number, S, I extends Number, S1, I1 extends Number, S2, I2 extends Number, T extends Date, T1 extends Date> {
    private long id;
    private String name;
    private int tagId;
    private String statusName;
    private int price;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTagId() {
        return tagId;
    }

    public String getStatusName() {
        return statusName;
    }

    public int getPrice() {
        return price;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getAmount() {
        return amount;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public ProductList(long id, String name, int tagId, String statusName, int price, String ownerName, int amount, Timestamp created_at, Timestamp updated_at) {
        this.id = id;
        this.name = name;
        this.tagId = tagId;
        this.statusName = statusName;
        this.price = price;
        this.ownerName = ownerName;
        this.amount = amount;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    private String ownerName;
    private int amount;
    private Timestamp created_at;
    private Timestamp updated_at;


}
