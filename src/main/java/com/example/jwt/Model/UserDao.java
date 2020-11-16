package com.example.jwt.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "User")
public class UserDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;
    @Column
    private String username;
    @Column
    @JsonIgnore
    private String password;

    @ManyToOne()
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @JsonIgnore
    private RoleDao roleDao;

    @ManyToOne()
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @JsonIgnore
    private StatusDao statusDao;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonIgnore
    private Timestamp updatedAt;

    @OneToOne
    @JoinColumn(name = "jwt_id", referencedColumnName = "id")
    @JsonIgnore
    private JwtDao jwtDao;

    public JwtDao getJwtDao() {
        return jwtDao;
    }

    public void setJwtDao(JwtDao jwtDao) {
        this.jwtDao = jwtDao;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @PrePersist
    void createdAt() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleDao getRoleDao() {
        return roleDao;
    }

    public String getRole() {
        return roleDao.getName();
    }

    public String getStatus() {
        return statusDao.getName();
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public StatusDao getStatusDao() {
        return statusDao;
    }

    public void setStatusDao(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
