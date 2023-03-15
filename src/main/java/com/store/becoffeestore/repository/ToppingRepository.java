package com.store.becoffeestore.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.store.becoffeestore.entity.Topping;

public interface ToppingRepository extends CrudRepository<Topping, Long> {

    List<Topping> findAll();
    List<Topping> findAllByIdIn(List<Long> id);
}
