package com.example.jwt.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_order")
public class OrdersDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "product_id")
    long productId;

    @Column
    int amount;

    @Column(name = "shopper_id")
    long shopperId;

    @Column
    int price;

    @Column(name = "owner_id")
    long ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonIgnore
    private Timestamp updatedAt;

    @ManyToOne()
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @JsonIgnore
    private StatusDao statusDao;



}
