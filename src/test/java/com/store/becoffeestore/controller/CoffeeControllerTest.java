package com.store.becoffeestore.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.entity.Coffee;
import com.store.becoffeestore.repository.CoffeeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.completableFuture;

@SpringBootTest(classes = BeCoffeeStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoffeeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @BeforeEach
    void setup() {
        coffeeRepository.deleteAll();
    }

    @Test
    void test_insert_coffee_is_successful(){
        HttpEntity<String> httpEntity = new HttpEntity<>(getCoffeeDtoBody(BigDecimal.TEN), getHeaders());
        String url = "http://localhost:" + port + "/coffee";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity,
            CoffeeDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CoffeeDto coffeeDto = responseEntity.getBody();
        assertThat(coffeeDto).isNotNull();
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeDto.getId()).isEqualTo(coffeeList.get(coffeeList.size() -1).getId());
        assertThat(coffeeDto.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(coffeeDto.getName()).isEqualTo("Latte");
    }

    @Test
    void test_update_coffee_is_successful(){
        Coffee coffee = createCoffee("Black", BigDecimal.ONE);

        HttpEntity<String> httpEntity = new HttpEntity<>(getCoffeeDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/coffee/"+coffee.getId();
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity,
            CoffeeDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CoffeeDto coffeeDto = responseEntity.getBody();
        assertThat(coffeeDto).isNotNull();
        assertThat(coffeeDto.getId()).isEqualTo(coffee.getId());
        assertThat(coffeeDto.getPrice()).isEqualTo(BigDecimal.ONE);
        assertThat(coffeeDto.getName()).isEqualTo("Latte");
    }

    @Test
    void test_get_coffee_is_successful(){
       Coffee coffee = createCoffee("Black", BigDecimal.ONE);

        HttpEntity<String> httpEntity = new HttpEntity<>(getCoffeeDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/coffee/"+coffee.getId();
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            CoffeeDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CoffeeDto coffeeDto = responseEntity.getBody();
        assertThat(coffeeDto).isNotNull();
        assertThat(coffeeDto.getId()).isEqualTo(coffee.getId());
        assertThat(coffeeDto.getPrice()).isEqualTo(new BigDecimal("1.00"));
        assertThat(coffeeDto.getName()).isEqualTo("Black");
    }

    @Test
    void test_delete_coffee_is_successful(){
        Coffee coffee = createCoffee("Black", BigDecimal.ONE);

        HttpEntity<String> httpEntity = new HttpEntity<>(getCoffeeDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/coffee/"+coffee.getId();
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity,
            CoffeeDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CoffeeDto coffeeDto = responseEntity.getBody();
        assertThat(coffeeDto).isNotNull();
        assertThat(coffeeDto.getId()).isEqualTo(coffee.getId());
        assertThat(coffeeDto.getPrice()).isEqualTo(new BigDecimal("1.00"));
        assertThat(coffeeDto.getName()).isEqualTo("Black");
        Optional<Coffee> coffeeOptional = coffeeRepository.findById(coffee.getId());
        assertThat(coffeeOptional).isEmpty();
    }

    @Test
    void test_get_coffee_list_is_successful(){
        Coffee cofee1 = createCoffee("Black", BigDecimal.ONE);
        Coffee cofee2 = createCoffee("Latte", BigDecimal.TEN);

        HttpEntity<String> httpEntity = new HttpEntity<>(getCoffeeDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/coffee";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            CoffeeDto[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CoffeeDto[] coffeeDtoList = responseEntity.getBody();
        assertThat(coffeeDtoList).isNotNull();
        assertThat(coffeeDtoList).hasSize(2);
        assertThat(coffeeDtoList[0].getId()).isEqualTo(cofee1.getId());
        assertThat(coffeeDtoList[0].getPrice()).isEqualTo(new BigDecimal("1.00"));
        assertThat(coffeeDtoList[0].getName()).isEqualTo("Black");
        assertThat(coffeeDtoList[1].getId()).isEqualTo(cofee2.getId());
        assertThat(coffeeDtoList[1].getPrice()).isEqualTo(new BigDecimal("10.00"));
        assertThat(coffeeDtoList[1].getName()).isEqualTo("Latte");
    }

    private Coffee createCoffee(String name, BigDecimal price) {
        Coffee coffee = new Coffee();
        coffee.setName(name);
        coffee.setPrice(price);
        return coffeeRepository.save(coffee);
    }

    private String getCoffeeDtoBody(BigDecimal price) {
        final CoffeeDto coffeeDto = new CoffeeDto();
        coffeeDto.setName("Latte");
        coffeeDto.setPrice(price);
        try {
            return objectMapper.writeValueAsString(coffeeDto);
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
