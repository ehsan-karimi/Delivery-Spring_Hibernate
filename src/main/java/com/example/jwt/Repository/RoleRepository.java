package com.example.jwt.Repository;

import com.example.jwt.Model.RoleDao;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleDao, Integer> {
    RoleDao findByName(String name);
}
