package com.example.jwt.Model.Order;

import java.sql.Timestamp;
import java.util.Date;

public class OrderStatusList<S, T extends Date> {

    String status;
    Timestamp date;

    public String getStatus() {
        return status;
    }

    public Timestamp getDate() {
        return date;
    }

    public OrderStatusList(String status, Timestamp date) {
        this.status = status;
        this.date = date;
    }
}
