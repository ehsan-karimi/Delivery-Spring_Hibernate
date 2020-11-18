package com.example.jwt.Model.Order;


import com.example.jwt.Model.Product.ProductsDao;
import com.example.jwt.Model.User.UserDao;

import java.sql.Timestamp;
import java.util.Date;

public class OrdersList<L extends Number, P, I extends Number, U, I1 extends Number, S, I2 extends Number, I3 extends Number, T extends Date, T1 extends Date, O> {
    long id;
    ProductsDao productsId;
    int amount;
    UserDao shopper;
    int price;
    String address;
    int phone;
    int postalCode;
    Timestamp created_at;
    Timestamp updated_at;
    OrderStatusDao orderStatus;

    public OrdersList(long id, ProductsDao productsId, int amount, UserDao shopper, int price, String address, int phone, int postalCode, Timestamp created_at, Timestamp updated_at, OrderStatusDao orderStatus) {
        this.id = id;
        this.productsId = productsId;
        this.amount = amount;
        this.shopper = shopper;
        this.price = price;
        this.address = address;
        this.phone = phone;
        this.postalCode = postalCode;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.orderStatus = orderStatus;
    }

    public long getId() {
        return id;
    }

    public ProductsDao getProductsId() {
        return productsId;
    }

    public int getAmount() {
        return amount;
    }

    public UserDao getShopper() {
        return shopper;
    }

    public int getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public int getPhone() {
        return phone;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public OrderStatusDao getOrderStatus() {
        return orderStatus;
    }
}
