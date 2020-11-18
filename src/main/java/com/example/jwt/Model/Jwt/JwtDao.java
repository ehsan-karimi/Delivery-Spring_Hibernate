package com.example.jwt.Model.Jwt;

import com.example.jwt.Model.StatusDao;
import com.example.jwt.Model.User.UserDao;

import javax.persistence.*;

@Entity
@Table(name = "user_jwt")
public class JwtDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, updatable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserDao userDao;

    @ManyToOne()
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private StatusDao statusDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public StatusDao getStatusDao() {
        return statusDao;
    }

    public void setStatusDao(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
