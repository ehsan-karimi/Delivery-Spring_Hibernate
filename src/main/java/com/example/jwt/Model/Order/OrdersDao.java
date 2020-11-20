package com.example.jwt.Model.Order;

import com.example.jwt.Model.Product.ProductsDao;
import com.example.jwt.Model.User.UserDao;
import com.fasterxml.jackson.annotation.JsonFormat;
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


    @ManyToOne
    @JoinColumn(name = "products_id", referencedColumnName = "id")
    @JsonIgnore
    ProductsDao productsId;

    @Column
    int amount;

    @ManyToOne
    @JoinColumn(name = "shopper_id", referencedColumnName = "id")
    @JsonIgnore
    UserDao shopper;

    @Column
    int price;

    @Column
    String address;

    @Column
    int phone;

    @Column(name = "postal_code")
    int postalCode;

//    @ManyToOne
//    @JoinColumn(name = "owner_id")
//    @JsonIgnore
//    ProductsDao ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_status_id", referencedColumnName = "id")
    @JsonIgnore
    private OrderStatusDao orderStatusDao;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productsId.getId();
    }

    public ProductsDao getProductsId() {
        return productsId;
    }

    @PrePersist
    void createdAt() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void setProductsId(ProductsDao productsId) {
        this.productsId = productsId;
    }

    public String getShopperName() {
        return shopper.getUsername();
    }

    public UserDao getShopper() {
        return shopper;
    }

    public void setShopper(UserDao shopper) {
        this.shopper = shopper;
    }

//    public ProductsDao getOwnerId() {
//        return ownerId;
//    }
//
//    public void setOwnerId(ProductsDao ownerId) {
//        this.ownerId = ownerId;
//    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
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

    public OrderStatusDao getOrderStatusDao() {
        return orderStatusDao;
    }

    public String getOrderStatusName() {
        return orderStatusDao.getStatusDao().getName();
    }

    public void setOrderStatusDao(OrderStatusDao orderStatusDao) {
        this.orderStatusDao = orderStatusDao;
    }
}
