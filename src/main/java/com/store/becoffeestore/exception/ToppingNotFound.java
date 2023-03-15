package com.store.becoffeestore.exception;

public class ToppingNotFound extends Exception {

    public ToppingNotFound() {
        super("Topping cannot be found");
    }
}
