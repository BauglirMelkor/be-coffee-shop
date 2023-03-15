package com.store.becoffeestore.service;

import org.mapstruct.Mapper;

import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.dto.result.SalesDto;
import com.store.becoffeestore.entity.Coffee;
import com.store.becoffeestore.entity.Sales;
import com.store.becoffeestore.entity.Topping;

@Mapper(componentModel = "spring")
public interface MappingService {

    CoffeeDto coffeeEntityToDTO(Coffee coffee);

    ToppingDto toppingEntityToDTO(Topping topping);

    SalesDto salesEntityToDTO(Sales sales);

    Coffee coffeeDtoToEntity(CoffeeDto coffeeDto);

    Topping toppingDtoToEntity(ToppingDto toppingDto);

}
