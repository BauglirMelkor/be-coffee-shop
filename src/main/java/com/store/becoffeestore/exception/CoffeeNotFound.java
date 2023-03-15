package com.store.becoffeestore.exception;

public class CoffeeNotFound extends Exception {

    public CoffeeNotFound() {
        super("Coffee cannot be found");
    }
}
