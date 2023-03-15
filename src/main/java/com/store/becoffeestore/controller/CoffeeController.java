package com.store.becoffeestore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.service.CoffeeService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/coffee")
public class CoffeeController {

    private final CoffeeService coffeeService;

    public CoffeeController(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    @Operation(summary = "Inserts coffee to database")
    @PostMapping()
    public ResponseEntity<CoffeeDto> insertCoffee(@RequestBody CoffeeDto coffeeDto) {
        return
            ResponseEntity.ok(coffeeService.insertCoffee(coffeeDto));
    }

    @Operation(summary = "Updates existing coffee in the database")
    @PutMapping("/{id}")
    public ResponseEntity<CoffeeDto> updateCoffee(@RequestBody CoffeeDto coffeeDto, @PathVariable("id") Long id) throws CoffeeNotFound {
        return ResponseEntity.ok(coffeeService.updateCoffee(coffeeDto, id));
    }

    @Operation(summary = "Gets coffee by id")
    @GetMapping("/{id}")
    public ResponseEntity<CoffeeDto> getCoffee(@PathVariable("id") Long id) throws CoffeeNotFound {
        return ResponseEntity.ok(coffeeService.getCoffee(id));
    }

    @Operation(summary = "Gets all coffees in the database")
    @GetMapping()
    public ResponseEntity<List<CoffeeDto>> getAllCoffee() {
        return ResponseEntity.ok(coffeeService.getAllCoffee());
    }

    @Operation(summary = "Deletes the coffee using id")
    @DeleteMapping("/{id}")
    public ResponseEntity<CoffeeDto> deleteCoffee(@PathVariable("id") Long id) throws CoffeeNotFound {
        return ResponseEntity.ok(coffeeService.deleteCoffee(id));
    }

}
