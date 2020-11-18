package com.example.jwt.Repository;

import com.example.jwt.Model.Jwt.JwtDao;
import com.example.jwt.Model.User.UserDao;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<JwtDao, Integer> {
    JwtDao findByUserDao(UserDao userDao);
}
