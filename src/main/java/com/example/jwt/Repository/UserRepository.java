package com.example.jwt.Repository;

import com.example.jwt.Model.User.UserDao;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserDao, Integer> {
    UserDao findByUsername(String username);
    UserDao findById(long id);
}
