package com.store.becoffeestore.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.exception.ToppingNotFound;

@ControllerAdvice
public class NotFountExceptionHandler {

    @ExceptionHandler(CoffeeNotFound.class)
    @ResponseBody
    public ResponseEntity<Object> handleCoffeeNotFoundException(CoffeeNotFound ex) {
        return new ResponseEntity<>("Coffee not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ToppingNotFound.class)
    @ResponseBody
    public ResponseEntity<Object> handleToppingNotFoundException(ToppingNotFound ex) {
        return new ResponseEntity<>("Topping not found", HttpStatus.NOT_FOUND);
    }
}