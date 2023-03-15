package com.store.becoffeestore.dto.result;

import java.math.BigDecimal;
import java.util.List;

import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.dto.ToppingDto;

public class BasketItemDto {

    private CoffeeDto coffeeDto;
    private List<ToppingDto> toppingDtoList;
    private Integer amount;
    private BigDecimal totalCoffeePrice;
    private BigDecimal totalToppingPrice;
    private BigDecimal itemTotal;


    public CoffeeDto getCoffeeDto() {
        return coffeeDto;
    }

    public void setCoffeeDto(CoffeeDto coffeeDto) {
        this.coffeeDto = coffeeDto;
    }

    public BigDecimal getTotalCoffeePrice() {
        return totalCoffeePrice;
    }

    public void setTotalCoffeePrice(BigDecimal totalCoffeePrice) {
        this.totalCoffeePrice = totalCoffeePrice;
    }

    public BigDecimal getTotalToppingPrice() {
        return totalToppingPrice;
    }

    public void setTotalToppingPrice(BigDecimal totalToppingPrice) {
        this.totalToppingPrice = totalToppingPrice;
    }

    public List<ToppingDto> getToppingDtoList() {
        return toppingDtoList;
    }

    public void setToppingDtoList(List<ToppingDto> toppingDtoList) {
        this.toppingDtoList = toppingDtoList;
    }

    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
