package com.example.jwt.Repository;

import com.example.jwt.Model.TagDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<TagDao, Integer> {
    TagDao findById(int id);
    TagDao findByName(String tagName);
    List<TagDao> findAll();
}
