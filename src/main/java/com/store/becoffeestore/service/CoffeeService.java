package com.store.becoffeestore.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.entity.Coffee;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.repository.CoffeeRepository;

@Service
public class CoffeeService {

    Logger logger = LoggerFactory.getLogger(CoffeeService.class);

    private final CoffeeRepository coffeeRepository;
    private final MappingService mappingService;

    public CoffeeService(CoffeeRepository coffeeRepository, MappingService mappingService) {
        this.coffeeRepository = coffeeRepository;
        this.mappingService = mappingService;
    }

    public CoffeeDto insertCoffee(CoffeeDto coffeeDto) {
        Coffee coffee = mappingService.coffeeDtoToEntity(coffeeDto);
        coffeeRepository.save(coffee);
        logger.debug("Coffee is saved name: {} id: {}", coffee.getName(), coffee.getId());
        return mappingService.coffeeEntityToDTO(coffee);
    }

    public CoffeeDto getCoffee(Long id) throws CoffeeNotFound {
        Optional<Coffee> coffeeOptional = coffeeRepository.findById(id);
        Coffee coffee = coffeeOptional.orElseThrow(CoffeeNotFound::new);
        return mappingService.coffeeEntityToDTO(coffee);
    }

    public CoffeeDto deleteCoffee(Long id) throws CoffeeNotFound {
        Optional<Coffee> coffeeOptional = coffeeRepository.findById(id);
        Coffee coffee = coffeeOptional.orElseThrow(CoffeeNotFound::new);
        coffeeRepository.delete(coffee);
        logger.debug("Coffee is deleted name: {} id: {}", coffee.getName(), coffee.getId());
        return mappingService.coffeeEntityToDTO(coffee);
    }

    public CoffeeDto updateCoffee(CoffeeDto coffeeDto, Long id) throws CoffeeNotFound {
        Optional<Coffee> coffeeOptional = coffeeRepository.findById(id);
        final Coffee coffee = coffeeOptional.orElseThrow(CoffeeNotFound::new);
        coffee.setName(coffeeDto.getName());
        coffee.setPrice(coffeeDto.getPrice());
        final Coffee coffeeUpdated = coffeeRepository.save(coffee);
        logger.debug("Coffee is updated name: {} id: {}", coffee.getName(), coffee.getId());
        return mappingService.coffeeEntityToDTO(coffeeUpdated);
    }

    public List<CoffeeDto> getAllCoffee() {
        return coffeeRepository.findAll()
            .stream()
            .map(mappingService::coffeeEntityToDTO)
            .toList();
    }
}
