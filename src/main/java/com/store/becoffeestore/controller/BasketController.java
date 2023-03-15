package com.store.becoffeestore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.becoffeestore.dto.BasketDto;
import com.store.becoffeestore.dto.result.ResultBasketDto;
import com.store.becoffeestore.dto.result.SalesDto;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.exception.ToppingNotFound;
import com.store.becoffeestore.service.BasketService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @Operation(summary = "Adds products to basket")
    @PostMapping()
    public ResponseEntity<ResultBasketDto> addToBasket(@RequestBody BasketDto basketDto) throws CoffeeNotFound, ToppingNotFound {
        return
            ResponseEntity.ok(basketService.addToBasket(basketDto));
    }

    @Operation(summary = "Completes transaction and saves the basket content to database")
    @GetMapping("/buy")
    public ResponseEntity<List<SalesDto>> finalizeOrder() {
        return
            ResponseEntity.ok(basketService.finalizeOrder());
    }

    @Operation(summary = "Clears basket")
    @DeleteMapping()
    public ResponseEntity<ResultBasketDto> clearBasket() {
        return
            ResponseEntity.ok(basketService.clearBasket());
    }

    @Operation(summary = "Deletes product from basket using product id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResultBasketDto> deleteFromBasket(@PathVariable Long id) {
        return
            ResponseEntity.ok(basketService.deleteFromBasket(id));
    }
}
