package com.store.becoffeestore.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.store.becoffeestore.dto.BasketDto;
import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.dto.result.ResultBasketDto;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.exception.ToppingNotFound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class BasketServiceAddToBasketTest {

    private BasketService basketService;

    @Mock
    private CoffeeService coffeeService;

    @Mock
    private ToppingService toppingService;

    @Mock
    private MappingService mappingService;

    @Mock
    private SalesService salesService;

    @BeforeEach
    void setup() {
        basketService = new BasketService(coffeeService, toppingService, mappingService, salesService);
    }

    @Test
    void test_coffenotfound_exception_is_thrown() throws CoffeeNotFound {
        doThrow(CoffeeNotFound.class).when(coffeeService).getCoffee(1L);
        BasketDto basketDto = new BasketDto();
        basketDto.setCoffeeId(1L);
        basketDto.setAmount(1);
        assertThrows(CoffeeNotFound.class, () -> basketService.addToBasket(basketDto));
    }

    @Test
    void test_toppingnotfound_exception_is_thrown() throws ToppingNotFound, CoffeeNotFound {
        doThrow(ToppingNotFound.class).when(toppingService).getToppingList(List.of(1L));
        when(coffeeService.getCoffee(1L)).thenReturn(new CoffeeDto());
        BasketDto basketDto = new BasketDto();
        basketDto.setToppingIds(List.of(1L));
        basketDto.setCoffeeId(1L);
        basketDto.setAmount(1);
        assertThrows(ToppingNotFound.class, () -> basketService.addToBasket(basketDto));
    }

    @Test
    void test_that_coffee_and_toppings_added_to_basket_successfully() throws ToppingNotFound, CoffeeNotFound {
        CoffeeDto coffeeDto = new CoffeeDto();
        coffeeDto.setId(1L);
        coffeeDto.setName("Black Coffee");
        coffeeDto.setPrice(BigDecimal.valueOf(4));

        when(coffeeService.getCoffee(1L)).thenReturn(coffeeDto);

        ToppingDto toppingDto1 = new ToppingDto();
        toppingDto1.setName("Milk");
        toppingDto1.setPrice(BigDecimal.valueOf(2));
        toppingDto1.setId(1L);

        ToppingDto toppingDto2 = new ToppingDto();
        toppingDto2.setName("Hazelnut syrup");
        toppingDto2.setPrice(BigDecimal.valueOf(3));
        toppingDto2.setId(2L);

        when(toppingService.getToppingList(List.of(1L,2L))).thenReturn(List.of(toppingDto1, toppingDto2));

        BasketDto basketDto = new BasketDto();
        basketDto.setAmount(1);
        basketDto.setCoffeeId(1L);
        basketDto.setToppingIds(List.of(1L,2L));
        ResultBasketDto resultBasketDto = basketService.addToBasket(basketDto);

        assertThat(resultBasketDto.getBasketDtoList()).hasSize(1);
        assertThat(resultBasketDto.getBasketDtoList().get(0).getToppingDtoList()).hasSize(2);
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(BigDecimal.valueOf(0));

        basketService.finalizeOrder();
    }

    @Test
    void test_that_total_discount_applied_to_basket_successfully() throws ToppingNotFound, CoffeeNotFound {
        CoffeeDto coffeeDto = new CoffeeDto();
        coffeeDto.setId(1L);
        coffeeDto.setName("Latte");
        coffeeDto.setPrice(BigDecimal.valueOf(5));

        when(coffeeService.getCoffee(1L)).thenReturn(coffeeDto);

        ToppingDto toppingDto1 = new ToppingDto();
        toppingDto1.setName("Milk");
        toppingDto1.setPrice(BigDecimal.valueOf(3));
        toppingDto1.setId(1L);

        ToppingDto toppingDto2 = new ToppingDto();
        toppingDto2.setName("Chocolate sauce");
        toppingDto2.setPrice(BigDecimal.valueOf(5));
        toppingDto2.setId(2L);

        when(toppingService.getToppingList(List.of(1L,2L))).thenReturn(List.of(toppingDto1, toppingDto2));

        BasketDto basketDto = new BasketDto();
        basketDto.setAmount(1);
        basketDto.setCoffeeId(1L);
        basketDto.setToppingIds(List.of(1L,2L));

        basketService.addToBasket(basketDto);

        basketDto = new BasketDto();
        basketDto.setAmount(1);
        basketDto.setCoffeeId(1L);
        basketDto.setToppingIds(List.of(1L,2L));
        ResultBasketDto resultBasketDto = basketService.addToBasket(basketDto);

        assertThat(resultBasketDto.getBasketDtoList()).hasSize(2);
        assertThat(resultBasketDto.getBasketDtoList().get(0).getToppingDtoList()).hasSize(2);
        assertThat(resultBasketDto.getBasketDtoList().get(1).getToppingDtoList()).hasSize(2);
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(BigDecimal.valueOf(26));
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(new BigDecimal("19.50"));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(new BigDecimal("6.50"));
        assertThat(resultBasketDto.getDiscountDescription()).isEqualTo("Total Price Discount");

        basketService.clearBasket();
    }

    @Test
    void test_that_amount_discount_applied_to_basket_successfully()  throws ToppingNotFound, CoffeeNotFound {
        prepareBasketWithDifferentProducts();

        BasketDto basketDto = new BasketDto();
        basketDto.setAmount(1);
        basketDto.setCoffeeId(1L);
        basketDto.setToppingIds(List.of(1L));

        ResultBasketDto resultBasketDto = basketService.addToBasket(basketDto);

        assertThat(resultBasketDto.getBasketDtoList()).hasSize(1);
        assertThat(resultBasketDto.getBasketDtoList().get(0).getToppingDtoList()).hasSize(1);
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(new BigDecimal("2"));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(new BigDecimal("0"));
        assertThat(resultBasketDto.getDiscountDescription()).isEmpty();

        basketDto = new BasketDto();
        basketDto.setAmount(2);
        basketDto.setCoffeeId(3L);
        basketDto.setToppingIds(List.of(4L));

        resultBasketDto = basketService.addToBasket(basketDto);

        assertThat(resultBasketDto.getBasketDtoList()).hasSize(2);
        assertThat(resultBasketDto.getBasketDtoList().get(0).getToppingDtoList()).hasSize(1);
        assertThat(resultBasketDto.getBasketDtoList().get(1).getToppingDtoList()).hasSize(1);
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(BigDecimal.valueOf(10));
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(new BigDecimal("8"));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(new BigDecimal("2"));
        assertThat(resultBasketDto.getDiscountDescription()).isEqualTo("Total Amount Discount");

        basketService.clearBasket();
    }

    @Test
    void test_that_deletes_froms_basket_successfully() throws ToppingNotFound, CoffeeNotFound {
        prepareBasketWithDifferentProducts();

        BasketDto basketDto = new BasketDto();
        basketDto.setAmount(1);
        basketDto.setCoffeeId(1L);
        basketDto.setToppingIds(List.of(1L));

        basketService.addToBasket(basketDto);

        basketDto = new BasketDto();
        basketDto.setAmount(2);
        basketDto.setCoffeeId(3L);
        basketDto.setToppingIds(List.of(4L));

        basketService.addToBasket(basketDto);

        ResultBasketDto resultBasketDto = basketService.deleteFromBasket(1L);

        assertThat(resultBasketDto.getBasketDtoList()).hasSize(1);
        assertThat(resultBasketDto.getBasketDtoList().get(0).getToppingDtoList()).hasSize(1);
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(BigDecimal.valueOf(8));
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(new BigDecimal("8"));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(new BigDecimal("0"));
        assertThat(resultBasketDto.getDiscountDescription()).isEmpty();

    }

    void prepareBasketWithDifferentProducts() throws CoffeeNotFound, ToppingNotFound {
        CoffeeDto coffeeDto = new CoffeeDto();
        coffeeDto.setId(1L);
        coffeeDto.setName("Cheap Latte");
        coffeeDto.setPrice(BigDecimal.valueOf(1));

        CoffeeDto coffeeDto2 = new CoffeeDto();
        coffeeDto2.setId(3L);
        coffeeDto2.setName("Expensive Latte");
        coffeeDto2.setPrice(BigDecimal.valueOf(2));

        when(coffeeService.getCoffee(anyLong())).thenReturn(coffeeDto,coffeeDto2);

        ToppingDto toppingDto1 = new ToppingDto();
        toppingDto1.setName("Cheap Milk");
        toppingDto1.setPrice(BigDecimal.valueOf(1));
        toppingDto1.setId(1L);

        ToppingDto toppingDto2 = new ToppingDto();
        toppingDto2.setName("Expensive Milk");
        toppingDto2.setPrice(BigDecimal.valueOf(2));
        toppingDto2.setId(4L);

        when(toppingService.getToppingList(anyList())).thenReturn(List.of(toppingDto1),List.of(toppingDto2));
    }



}
