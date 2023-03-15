package com.store.becoffeestore.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.store.becoffeestore.dto.result.ProductDto;
import com.store.becoffeestore.entity.Sales;

public interface SalesRepository extends CrudRepository<Sales, Long> {

    @Query(name = "maxCoffeeQuery", nativeQuery = true)
    ProductDto findMaxSoldCoffee();

    @Query(name = "maxToppingQuery", nativeQuery = true)
    ProductDto findMaxSoldTopping();

}
