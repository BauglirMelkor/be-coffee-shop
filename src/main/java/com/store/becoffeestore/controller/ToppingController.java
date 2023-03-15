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

import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.exception.ToppingNotFound;
import com.store.becoffeestore.service.ToppingService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/topping")
public class ToppingController {

    private final ToppingService toppingService;

    public ToppingController(ToppingService toppingService) {
        this.toppingService = toppingService;
    }

    @Operation(summary = "Inserts topping to database")
    @PostMapping()
    public ToppingDto insertTopping(@RequestBody ToppingDto toppingDto) {
        return toppingService.insertTopping(toppingDto);
    }

    @Operation(summary = "Updates existing topping in the database")
    @PutMapping("/{id}")
    public ToppingDto updateTopping(@RequestBody ToppingDto toppingDto, @PathVariable("id") Long id) throws ToppingNotFound {
        return toppingService.updateTopping(toppingDto, id);
    }

    @Operation(summary = "Gets topping by id")
    @GetMapping("/{id}")
    public ToppingDto updateTopping(@PathVariable("id") Long id) throws ToppingNotFound {
        return toppingService.getTopping(id);
    }

    @Operation(summary = "Gets all toppings in the database")
    @GetMapping()
    public List<ToppingDto> getAllToppings() {
        return toppingService.getAllToppings();
    }

    @Operation(summary = "Deletes the topping using id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ToppingDto> deleteTopping(@PathVariable("id") Long id) throws ToppingNotFound {
        return ResponseEntity.ok(toppingService.deleteTopping(id));
    }

}
