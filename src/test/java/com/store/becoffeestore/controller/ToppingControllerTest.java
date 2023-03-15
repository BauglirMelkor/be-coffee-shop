package com.store.becoffeestore.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.entity.Topping;
import com.store.becoffeestore.repository.ToppingRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BeCoffeeStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ToppingControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ToppingRepository toppingRepository;

    @Test
    void test_insert_topping_is_successful(){
        HttpEntity<String> httpEntity = new HttpEntity<>(getToppingDtoBody(BigDecimal.TEN), getHeaders());
        String url = "http://localhost:" + port + "/topping";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity,
            ToppingDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ToppingDto toppingDto = responseEntity.getBody();
        assertThat(toppingDto).isNotNull();
        Long maxId = toppingRepository.findAll().stream().map(Topping::getId).max(Long::compare).orElse(0L);
        assertThat(maxId).isNotZero();
        assertThat(toppingDto.getId()).isEqualTo(maxId);
        assertThat(toppingDto.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(toppingDto.getName()).isEqualTo("Latte");
    }

    @Test
    void test_update_topping_is_successful(){
        Topping topping = createTopping("Black", BigDecimal.ONE);

        HttpEntity<String> httpEntity = new HttpEntity<>(getToppingDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/topping/"+topping.getId();
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity,
            ToppingDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ToppingDto toppingDto = responseEntity.getBody();
        assertThat(toppingDto).isNotNull();
        assertThat(toppingDto.getId()).isEqualTo(topping.getId());
        assertThat(toppingDto.getPrice()).isEqualTo(BigDecimal.ONE);
        assertThat(toppingDto.getName()).isEqualTo("Latte");
    }

    @Test
    void test_get_topping_is_successful(){
        Topping topping = createTopping("Black", BigDecimal.ONE);

        HttpEntity<String> httpEntity = new HttpEntity<>(getToppingDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/topping/"+topping.getId();
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            ToppingDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ToppingDto toppingDto = responseEntity.getBody();
        assertThat(toppingDto).isNotNull();
        assertThat(toppingDto.getId()).isEqualTo(topping.getId());
        assertThat(toppingDto.getPrice()).isEqualTo(new BigDecimal("1.00"));
        assertThat(toppingDto.getName()).isEqualTo("Black");
    }

    @Test
    void test_delete_topping_is_successful(){
        Topping topping = createTopping("Black", BigDecimal.ONE);

        HttpEntity<String> httpEntity = new HttpEntity<>(getToppingDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/topping/"+topping.getId();
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity,
            ToppingDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ToppingDto toppingDto = responseEntity.getBody();
        assertThat(toppingDto).isNotNull();
        assertThat(toppingDto.getId()).isEqualTo(topping.getId());
        assertThat(toppingDto.getPrice()).isEqualTo(new BigDecimal("1.00"));
        assertThat(toppingDto.getName()).isEqualTo("Black");
        Optional<Topping> toppingOptional = toppingRepository.findById(topping.getId());
        assertThat(toppingOptional).isEmpty();
    }

    @Test
    void test_get_topping_list_is_successful(){
        Topping topping1 = createTopping("Black", BigDecimal.ONE);
        Topping topping2 = createTopping("Latte", BigDecimal.TEN);

        HttpEntity<String> httpEntity = new HttpEntity<>(getToppingDtoBody(BigDecimal.ONE), getHeaders());
        String url = "http://localhost:" + port + "/topping";
        var responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
            ToppingDto[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ToppingDto[] toppingDtoList = responseEntity.getBody();
        assertThat(toppingDtoList).isNotNull();
        int size = toppingRepository.findAll().size();
        assertThat(size).isNotZero();
        assertThat(toppingDtoList).hasSize(size);
        assertThat(toppingDtoList[size-2].getId()).isEqualTo(topping1.getId());
        assertThat(toppingDtoList[size-2].getPrice()).isEqualTo(new BigDecimal("1.00"));
        assertThat(toppingDtoList[size-2].getName()).isEqualTo("Black");
        assertThat(toppingDtoList[size-1].getId()).isEqualTo(topping2.getId());
        assertThat(toppingDtoList[size-1].getPrice()).isEqualTo(new BigDecimal("10.00"));
        assertThat(toppingDtoList[size-1].getName()).isEqualTo("Latte");
    }

    private Topping createTopping(String name, BigDecimal price) {
        Topping topping = new Topping();
        topping.setName(name);
        topping.setPrice(price);
        return toppingRepository.save(topping);
    }

    private String getToppingDtoBody(BigDecimal price) {
        final ToppingDto toppingDto = new ToppingDto();
        toppingDto.setName("Latte");
        toppingDto.setPrice(price);
        try {
            return objectMapper.writeValueAsString(toppingDto);
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
