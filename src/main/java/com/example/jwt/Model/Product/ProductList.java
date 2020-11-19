package com.example.jwt.Model.Product;

import com.example.jwt.Model.StatusDao;
import com.example.jwt.Model.TagDao;
import com.example.jwt.Model.User.UserDao;

import java.sql.Timestamp;
import java.util.Date;

public class ProductList<L extends Number, S, T, S1, I extends Number, U, I1 extends Number, T1 extends Date, T2 extends Date> {
    private long id;
    private String name;
    private TagDao tagDao;
    private StatusDao statusDao;
    private int price;
    private UserDao ownerId;
    private int amount;
    private Timestamp created_at;
    private Timestamp updated_at;

    public ProductList(long id, String name, TagDao tagDao, StatusDao statusDao, int price, UserDao ownerId, int amount, Timestamp created_at, Timestamp updated_at) {
        this.id = id;
        this.name = name;
        this.tagDao = tagDao;
        this.statusDao = statusDao;
        this.price = price;
        this.ownerId = ownerId;
        this.amount = amount;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public StatusDao getStatusDao() {
        return statusDao;
    }

    public int getPrice() {
        return price;
    }

    public UserDao getOwnerId() {
        return ownerId;
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
}
