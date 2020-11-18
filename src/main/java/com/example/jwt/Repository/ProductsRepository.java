package com.example.jwt.Repository;

import com.example.jwt.Model.*;
import com.example.jwt.Model.Product.ProductsDao;
import com.example.jwt.Model.User.UserDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductsRepository extends CrudRepository<ProductsDao, Integer> {
    ProductsDao findById(long id);
    Iterable<ProductsDao> findAllByStatusDao(StatusDao statusDao);
    List<ProductsDao> findAllByOwnerId(UserDao id);
    List<ProductsDao> findAllByTagId(TagDao id);
}
