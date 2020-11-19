package com.example.jwt.Repository;

import com.example.jwt.Model.StatusDao;
import org.springframework.data.repository.CrudRepository;

public interface StatusRepository extends CrudRepository<StatusDao, Integer> {
    StatusDao findByName(String name);
}
