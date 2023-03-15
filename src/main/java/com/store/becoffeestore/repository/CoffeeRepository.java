package com.store.becoffeestore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.store.becoffeestore.entity.Coffee;

public interface CoffeeRepository extends CrudRepository<Coffee, Long> {

    List<Coffee> findAll();
}
