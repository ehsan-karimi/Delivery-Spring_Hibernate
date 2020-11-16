package com.example.jwt.Repository;

import com.example.jwt.Model.JwtDao;
import com.example.jwt.Model.ProductsDao;
import com.example.jwt.Model.StatusDao;
import org.springframework.data.repository.CrudRepository;

public interface ProductsRepository extends CrudRepository<ProductsDao, Integer> {
    ProductsDao findById(long id);
    Iterable<ProductsDao> findAllByStatusDao(StatusDao statusDao);
}
