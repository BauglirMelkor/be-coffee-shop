package com.store.becoffeestore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.dto.result.CoffeeSoldDto;
import com.store.becoffeestore.dto.result.ProductDto;
import com.store.becoffeestore.dto.result.ToppingSoldDto;
import com.store.becoffeestore.entity.Sales;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.exception.ToppingNotFound;
import com.store.becoffeestore.repository.SalesRepository;

@Service
public class SalesService {

    private final SalesRepository salesRepository;
    private final MappingService mappingService;
    private final CoffeeService coffeeService;
    private final ToppingService toppingService;

    public SalesService(SalesRepository salesRepository, MappingService mappingService, CoffeeService coffeeService, ToppingService toppingService) {
        this.salesRepository = salesRepository;
        this.mappingService = mappingService;
        this.coffeeService = coffeeService;
        this.toppingService = toppingService;
    }

    public CoffeeSoldDto getMaxSoldCoffee() throws CoffeeNotFound {
        ProductDto product = salesRepository.findMaxSoldCoffee();
        CoffeeDto coffeeDto = coffeeService.getCoffee(product.getId());
        CoffeeSoldDto coffeeSoldDto = new CoffeeSoldDto();
        coffeeSoldDto.setCoffeeDto(coffeeDto);
        coffeeSoldDto.setAmount(product.getAmount());
        return coffeeSoldDto;
    }

    public ToppingSoldDto getMaxSoldTopping() throws ToppingNotFound {
        ProductDto product = salesRepository.findMaxSoldTopping();
        ToppingDto toppingDto = toppingService.getTopping(product.getId());
        ToppingSoldDto toppingSoldDto = new ToppingSoldDto();
        toppingSoldDto.setToppingDto(toppingDto);
        toppingSoldDto.setAmount(product.getAmount());
        return toppingSoldDto;
    }

    public void insertSales(List<Sales> sales){
        salesRepository.saveAll(sales);
    }

}
