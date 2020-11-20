package com.example.jwt.Repository;

import com.example.jwt.Model.RoleDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<RoleDao, Integer> {
    RoleDao findByName(String name);
    List<RoleDao> findAll();
}
