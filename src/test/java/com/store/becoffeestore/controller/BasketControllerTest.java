package com.store.becoffeestore.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.becoffeestore.BeCoffeeStoreApplication;
import com.store.becoffeestore.dto.BasketDto;
import com.store.becoffeestore.dto.result.CoffeeSoldDto;
import com.store.becoffeestore.dto.result.ResultBasketDto;
import com.store.becoffeestore.dto.result.SalesDto;
import com.store.becoffeestore.dto.result.ToppingSoldDto;
import com.store.becoffeestore.entity.Coffee;
import com.store.becoffeestore.entity.Topping;
import com.store.becoffeestore.repository.CoffeeRepository;
import com.store.becoffeestore.repository.ToppingRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BeCoffeeStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BasketControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    @Test
    void test_add_to_basket_and_sell_successfully() {

        Coffee coffee1 = createCoffee("Latte", new BigDecimal("3"));

        Topping topping1 = createTopping("Milk", new BigDecimal("2"));
        Topping topping2 = createTopping("Chocolate sauce", new BigDecimal("5"));

        HttpEntity<String> httpEntity = new HttpEntity<>(getBasketDtoBody(coffee1.getId(), List.of(topping1.getId(), topping2.getId()), 2), getHeaders());
        String url = "http://localhost:" + port + "/basket";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity,
            ResultBasketDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResultBasketDto resultBasketDto = responseEntity.getBody();
        assertThat(resultBasketDto).isNotNull();
        assertThat(resultBasketDto.getBasketDtoList()).hasSize(1);
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(new BigDecimal("15.0000"));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(new BigDecimal("5.0000"));
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(new BigDecimal("20.00"));

        Coffee coffee2 = createCoffee("Black", new BigDecimal("2"));
        Topping topping3 = createTopping("Lemon", new BigDecimal("5"));
        httpEntity = new HttpEntity<>(getBasketDtoBody(coffee2.getId(), List.of(topping1.getId(), topping3.getId()), 3), getHeaders());

        responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity,
            ResultBasketDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        resultBasketDto = responseEntity.getBody();
        assertThat(resultBasketDto).isNotNull();
        assertThat(resultBasketDto.getBasketDtoList()).hasSize(2);
        assertThat(resultBasketDto.getTotalPrice()).isEqualTo(new BigDecimal("35.2500"));
        assertThat(resultBasketDto.getTotalDiscount()).isEqualTo(new BigDecimal("11.7500"));
        assertThat(resultBasketDto.getPriceBeforeDiscount()).isEqualTo(new BigDecimal("47.00"));

        url = "http://localhost:" + port + "/basket/buy";
        var salesResult = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            SalesDto[].class);

        assertThat(salesResult).isNotNull();

        List<SalesDto> salesList = Arrays.asList(salesResult.getBody());

        assertThat(salesList).hasSize(6);
        assertThat(salesList.stream().filter(sale -> sale.getProduct().getName().equals("Latte")).findFirst().get().getAmount()).isEqualTo(2);
        assertThat(salesList.stream().filter(sale -> sale.getProduct().getName().equals("Chocolate sauce")).findFirst().get().getAmount()).isEqualTo(2);
        assertThat(salesList.stream().filter(sale -> sale.getProduct().getName().equals("Black")).findFirst().get().getAmount()).isEqualTo(3);
        assertThat(salesList.stream().filter(sale -> sale.getProduct().getName().equals("Lemon")).findFirst().get().getAmount()).isEqualTo(3);
        assertThat(salesList.stream().filter(sale -> sale.getProduct().getName().equals("Milk")).toList().get(0).getAmount()).isEqualTo(2);
        assertThat(salesList.stream().filter(sale -> sale.getProduct().getName().equals("Milk")).toList().get(1).getAmount()).isEqualTo(3);

        url = "http://localhost:" + port + "/sales/coffee";
        var coffeeDtoResponseEntity = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            CoffeeSoldDto.class);

        var maxSoldCoffee = coffeeDtoResponseEntity.getBody();
        assertThat(maxSoldCoffee).isNotNull();
        assertThat(maxSoldCoffee.getCoffeeDto().getId()).isEqualTo(coffee2.getId());
        assertThat(maxSoldCoffee.getAmount()).isEqualTo(3);

        url = "http://localhost:" + port + "/sales/topping";
        var toppingDtoResponseEntity = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            ToppingSoldDto.class);

        var maxSoldTopping = toppingDtoResponseEntity.getBody();
        assertThat(maxSoldTopping).isNotNull();
        assertThat(maxSoldTopping.getToppingDto().getId()).isEqualTo(topping1.getId());
        assertThat(maxSoldTopping.getAmount()).isEqualTo(5);

    }

    @Test
    void test_add_to_basket_returns_coffee_not_found() {

        Topping topping1 = createTopping("Hazelnut syrup", new BigDecimal("3"));

        HttpEntity<String> httpEntity = new HttpEntity<>(getBasketDtoBody(100L, List.of(topping1.getId()), 2), getHeaders());
        String url = "http://localhost:" + port + "/basket";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity,
            String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Coffee not found");
    }

    @Test
    void test_add_to_basket_returns_topping_not_found() {

        Coffee coffee1 = createCoffee("Mocha", new BigDecimal("6"));

        HttpEntity<String> httpEntity = new HttpEntity<>(getBasketDtoBody(coffee1.getId(), List.of(100L), 2), getHeaders());
        String url = "http://localhost:" + port + "/basket";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity,
            String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Topping not found");
    }

    private Coffee createCoffee(String name, BigDecimal price) {
        Coffee coffee = new Coffee();
        coffee.setName(name);
        coffee.setPrice(price);
        return coffeeRepository.save(coffee);
    }

    private Topping createTopping(String name, BigDecimal price) {
        Topping topping = new Topping();
        topping.setName(name);
        topping.setPrice(price);
        return toppingRepository.save(topping);
    }

    private String getBasketDtoBody(Long coffeeId, List<Long> toppingList, Integer amount) {
        final BasketDto basketDto = new BasketDto();
        basketDto.setCoffeeId(coffeeId);
        basketDto.setToppingIds(toppingList);
        basketDto.setAmount(amount);
        try {
            return objectMapper.writeValueAsString(basketDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

}
