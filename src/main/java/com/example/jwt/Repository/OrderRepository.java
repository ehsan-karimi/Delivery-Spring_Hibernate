package com.example.jwt.Repository;

import com.example.jwt.Model.*;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrdersDao, Integer> {
    OrdersDao findById(long id);
    Iterable<OrdersDao> findAllByShopper_id(UserDao id);
}
