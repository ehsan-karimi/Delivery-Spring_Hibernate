package com.example.jwt.Repository;

import com.example.jwt.Model.Order.OrderStatusDao;
import com.example.jwt.Model.Order.OrdersDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderStatusRepository extends CrudRepository<OrderStatusDao, Integer> {
    List<OrderStatusDao> findByOrdersDao(OrdersDao orderId);
    Iterable<OrderStatusDao> findAllByOrdersDao(OrdersDao id);
}
