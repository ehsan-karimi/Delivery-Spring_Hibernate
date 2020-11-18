package com.example.jwt.Repository;

import com.example.jwt.Model.Order.OrdersDao;
import com.example.jwt.Model.Product.ProductsDao;
import com.example.jwt.Model.User.UserDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<OrdersDao, Integer> {
    OrdersDao findById(long id);
    List<OrdersDao> findAllByShopper(UserDao id);
    List<OrdersDao> findAllByProductsId(ProductsDao id);

}
