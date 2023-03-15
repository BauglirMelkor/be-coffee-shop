package com.store.becoffeestore.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.store.becoffeestore.dto.result.BasketItemDto;
import com.store.becoffeestore.dto.result.ResultBasketDto;

import static org.assertj.core.api.Assertions.assertThat;

class BasketServiceDiscountTest {

    private BasketService basketService;

    @BeforeEach
    void setup() {
        basketService = new BasketService(null, null, null, null);
    }

    @Test
    void test_that_amount_discount_is_applied() {
        BasketItemDto basketItemDto1 = new BasketItemDto();
        basketItemDto1.setAmount(1);
        basketItemDto1.setItemTotal(BigDecimal.valueOf(5));

        BasketItemDto basketItemDto2 = new BasketItemDto();
        basketItemDto2.setAmount(2);
        basketItemDto2.setItemTotal(BigDecimal.valueOf(10));

        ResultBasketDto resultBasketDto = basketService.applyDiscount(List.of(basketItemDto1, basketItemDto2));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    void test_that_total_discount_is_applied() {
        BasketItemDto basketItemDto1 = new BasketItemDto();
        basketItemDto1.setAmount(1);
        basketItemDto1.setItemTotal(BigDecimal.valueOf(2));

        BasketItemDto basketItemDto2 = new BasketItemDto();
        basketItemDto2.setAmount(2);
        basketItemDto2.setItemTotal(BigDecimal.valueOf(3));

        BasketItemDto basketItemDto3 = new BasketItemDto();
        basketItemDto3.setAmount(1);
        basketItemDto3.setItemTotal(BigDecimal.valueOf(3));

        BasketItemDto basketItemDto4 = new BasketItemDto();
        basketItemDto4.setAmount(2);
        basketItemDto4.setItemTotal(BigDecimal.valueOf(4));

        ResultBasketDto resultBasketDto = basketService.applyDiscount(List.of(basketItemDto1, basketItemDto2, basketItemDto3, basketItemDto4));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(resultBasketDto.getPriceBeforeDiscount().multiply(BigDecimal.valueOf(0.25)));
    }

    @Test
    void test_that_no_discount_is_applied() {
        BasketItemDto basketItemDto1 = new BasketItemDto();
        basketItemDto1.setAmount(1);
        basketItemDto1.setItemTotal(BigDecimal.valueOf(5));

        BasketItemDto basketItemDto2 = new BasketItemDto();
        basketItemDto2.setAmount(1);
        basketItemDto2.setItemTotal(BigDecimal.valueOf(3));

        ResultBasketDto resultBasketDto = basketService.applyDiscount(List.of(basketItemDto1, basketItemDto2));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(BigDecimal.ZERO);
    }

}
