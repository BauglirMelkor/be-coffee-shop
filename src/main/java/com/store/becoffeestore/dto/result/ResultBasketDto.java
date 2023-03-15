package com.store.becoffeestore.dto.result;

import java.math.BigDecimal;
import java.util.List;

public class ResultBasketDto {

    private BigDecimal priceBeforeDiscount;

    private BigDecimal totalDiscount;

    private BigDecimal totalPrice;

    private String discountDescription;

    private List<BasketItemDto> basketDtoList;

    public BigDecimal getPriceBeforeDiscount() {
        return priceBeforeDiscount;
    }

    public void setPriceBeforeDiscount(BigDecimal priceBeforeDiscount) {
        this.priceBeforeDiscount = priceBeforeDiscount;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<BasketItemDto> getBasketDtoList() {
        return basketDtoList;
    }

    public void setBasketDtoList(List<BasketItemDto> basketDtoList) {
        this.basketDtoList = basketDtoList;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

}
