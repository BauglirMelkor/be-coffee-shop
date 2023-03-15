package com.store.becoffeestore.dto.result;

import com.store.becoffeestore.dto.ToppingDto;

public class ToppingSoldDto {

    private ToppingDto toppingDto;
    private Integer amount;

    public ToppingDto getToppingDto() {
        return toppingDto;
    }

    public void setToppingDto(ToppingDto toppingDto) {
        this.toppingDto = toppingDto;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
