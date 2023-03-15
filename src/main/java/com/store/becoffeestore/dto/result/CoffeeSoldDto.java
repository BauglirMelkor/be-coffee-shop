package com.store.becoffeestore.dto.result;

import com.store.becoffeestore.dto.CoffeeDto;

public class CoffeeSoldDto {

    private CoffeeDto coffeeDto;
    private Integer amount;

    public CoffeeDto getCoffeeDto() {
        return coffeeDto;
    }

    public void setCoffeeDto(CoffeeDto coffeeDto) {
        this.coffeeDto = coffeeDto;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
