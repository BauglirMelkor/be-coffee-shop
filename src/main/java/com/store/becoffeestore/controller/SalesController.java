package com.store.becoffeestore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.becoffeestore.dto.result.CoffeeSoldDto;
import com.store.becoffeestore.dto.result.ToppingSoldDto;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.exception.ToppingNotFound;
import com.store.becoffeestore.service.SalesService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @Operation(summary = "Gets the best selling coffee")
    @GetMapping("/coffee")
    public ResponseEntity<CoffeeSoldDto> getMaxSoldCoffee() throws CoffeeNotFound {
        return ResponseEntity.ok(salesService.getMaxSoldCoffee());
    }

    @Operation(summary = "Gets the best selling topping")
    @GetMapping("/topping")
    public ResponseEntity<ToppingSoldDto> getMaxSoldTopping() throws ToppingNotFound {
        return ResponseEntity.ok(salesService.getMaxSoldTopping());
    }
}
