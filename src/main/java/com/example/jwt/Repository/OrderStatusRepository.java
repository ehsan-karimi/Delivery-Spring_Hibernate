package com.example.jwt.Repository;

import com.example.jwt.Model.JwtDao;
import com.example.jwt.Model.OrderStatusDao;
import org.springframework.data.repository.CrudRepository;

public interface OrderStatusRepository extends CrudRepository<OrderStatusDao, Integer> {
}
