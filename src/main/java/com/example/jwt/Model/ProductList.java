package com.example.jwt.Model;

import java.sql.Timestamp;
import java.util.Date;

public class ProductList<L extends Number, S, T, S1, I extends Number, L1 extends Number, I1 extends Number, T1 extends Date, T2 extends Date> {
    long id;
    String name;
    TagDao tagDao;
    StatusDao statusDao;
    int price;
    long ownerId;
    int amount;
    Timestamp created_at;
    Timestamp updated_at;

    public ProductList(long id, String name, TagDao tagDao, StatusDao statusDao, int price, long ownerId, int amount, Timestamp created_at, Timestamp updated_at) {
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

    public long getOwnerId() {
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
