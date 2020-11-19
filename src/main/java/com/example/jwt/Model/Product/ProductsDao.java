package com.example.jwt.Model.Product;

import com.example.jwt.Model.StatusDao;
import com.example.jwt.Model.TagDao;
import com.example.jwt.Model.User.UserDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_products")
public class ProductsDao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private int price;

    @ManyToOne()
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    @JsonIgnore
    private TagDao tagId;

//    @Column(name = "owner_id")
//    @JsonIgnore
//    private long ownerId;
//
//    public long getOwnerId() {
//        return ownerId;
//    }
//
//    public void setOwnerId(long ownerId) {
//        this.ownerId = ownerId;
//    }

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", unique = false)
    @JsonIgnore
    private UserDao ownerId;

    @Column
    private int amount;

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

    @PrePersist
    void createdAt() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public UserDao getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserDao ownerId) {
        this.ownerId = ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TagDao getTagId() {
        return tagId;
    }

    public int getTag_id() {
        return tagId.getId();
    }

    public void setTagId(TagDao tagId) {
        this.tagId = tagId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StatusDao getStatusDao() {
        return statusDao;
    }

    public String getStatusName() {
        return statusDao.getName();
    }

    public void setStatusDao(StatusDao statusDao) {
        this.statusDao = statusDao;
    }
}
