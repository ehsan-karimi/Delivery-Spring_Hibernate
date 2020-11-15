package com.example.jwt.Repository;

import com.example.jwt.Model.JwtDao;
import com.example.jwt.Model.RoleDao;
import com.example.jwt.Model.UserDao;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<JwtDao, Integer> {
    JwtDao findByUserDao(UserDao userDao);
}
