package com.store.becoffeestore.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.store.becoffeestore.dto.BasketDto;
import com.store.becoffeestore.dto.CoffeeDto;
import com.store.becoffeestore.dto.result.BasketItemDto;
import com.store.becoffeestore.dto.result.ResultBasketDto;
import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.dto.result.SalesDto;
import com.store.becoffeestore.entity.Sales;
import com.store.becoffeestore.exception.CoffeeNotFound;
import com.store.becoffeestore.exception.ToppingNotFound;

@Service
public class BasketService {

    Logger logger = LoggerFactory.getLogger(BasketService.class);

    private final CoffeeService coffeeService;
    private final ToppingService toppingService;
    private final MappingService mappingService;
    private final SalesService salesService;
    //It would be better to return it to the user in every request instead of storing it in the variable.
    //Storing it inside an object will cause issues in case of multiple request from different users.
    private List<BasketItemDto> basketItemDtoList = new ArrayList<>();

    public BasketService(CoffeeService coffeeService, ToppingService toppingService, MappingService mappingService, SalesService salesService) {
        this.coffeeService = coffeeService;
        this.toppingService = toppingService;
        this.mappingService = mappingService;
        this.salesService = salesService;
    }

    public ResultBasketDto addToBasket(BasketDto basketDto) throws CoffeeNotFound, ToppingNotFound {
        CoffeeDto coffeeDto = coffeeService.getCoffee(basketDto.getCoffeeId());
        List<ToppingDto> toppingDtoList = toppingService.getToppingList(basketDto.getToppingIds());
        BigDecimal coffeePrice = coffeeDto.getPrice().multiply(BigDecimal.valueOf(basketDto.getAmount()));
        BigDecimal toppingPrice = toppingDtoList
            .stream()
            .map(ToppingDto::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .multiply(BigDecimal.valueOf(basketDto.getAmount()));

        BasketItemDto basketItemDto = prepareBasketItem(basketDto, coffeeDto, toppingDtoList, coffeePrice, toppingPrice);
        basketItemDtoList.add(basketItemDto);
        return applyDiscount(basketItemDtoList);
    }

    public BasketItemDto prepareBasketItem(BasketDto basketDto, CoffeeDto coffeeDto, List<ToppingDto> toppingDtoList, BigDecimal coffeePrice,
        BigDecimal toppingPrice) {
        BasketItemDto basketItemDto = new BasketItemDto();
        basketItemDto.setAmount(basketDto.getAmount());
        basketItemDto.setCoffeeDto(coffeeDto);
        basketItemDto.setToppingDtoList(toppingDtoList);
        basketItemDto.setTotalCoffeePrice(coffeePrice);
        basketItemDto.setTotalToppingPrice(toppingPrice);
        basketItemDto.setItemTotal(coffeePrice.add(toppingPrice));
        return basketItemDto;
    }

    public ResultBasketDto applyDiscount(List<BasketItemDto> basketItemDtoList) {
        Integer totalNumberOfItems = basketItemDtoList.stream().map(BasketItemDto::getAmount).reduce(0, Integer::sum);
        BigDecimal totalPrice = basketItemDtoList.stream().map(BasketItemDto::getItemTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        ResultBasketDto resultBasketDto = new ResultBasketDto();
        resultBasketDto.setBasketDtoList(basketItemDtoList);
        resultBasketDto.setPriceBeforeDiscount(totalPrice);
        BigDecimal amountDiscount = BigDecimal.ZERO;
        if (totalNumberOfItems >= 3) {
            amountDiscount = basketItemDtoList.stream().map(basketItemDto ->
                    basketItemDto.getItemTotal().divide(BigDecimal.valueOf(basketItemDto.getAmount())))
                .min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            logger.debug("Total Number Of Items Discount Applied");
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        if (totalPrice.compareTo(BigDecimal.valueOf(12)) >= 0) {
            totalDiscount = totalPrice.multiply(BigDecimal.valueOf(0.25));
        }

        if (amountDiscount.compareTo(totalDiscount) >= 0) {
            totalPrice = totalPrice.subtract(amountDiscount);
            resultBasketDto.setTotalDiscount(amountDiscount);
            resultBasketDto.setDiscountDescription(amountDiscount.compareTo(BigDecimal.ZERO) > 0 ? "Total Amount Discount" : "");
            logger.debug("Total Number Of Items Discount Applied, {}", amountDiscount);
        } else {
            totalPrice = totalPrice.subtract(totalDiscount);
            resultBasketDto.setTotalDiscount(totalDiscount);
            resultBasketDto.setDiscountDescription(totalDiscount.compareTo(BigDecimal.ZERO) > 0 ?"Total Price Discount" : "");
            logger.debug("Total Price Discount Applied, {}", totalDiscount);
        }
        resultBasketDto.setTotalPrice(totalPrice);
        return resultBasketDto;
    }

    public List<SalesDto> finalizeOrder() {
        logger.debug("Order is finalizing");
        List<Sales> salesList = new ArrayList<>();
        for(BasketItemDto basketItemDto: basketItemDtoList) {
            Sales sales = new Sales();
            sales.setAmount(basketItemDto.getAmount());
            sales.setProduct(mappingService.coffeeDtoToEntity(basketItemDto.getCoffeeDto()));
            salesList.add(sales);
            for(ToppingDto toppingDto: basketItemDto.getToppingDtoList()) {
                sales = new Sales();
                sales.setAmount(basketItemDto.getAmount());
                sales.setProduct(mappingService.toppingDtoToEntity(toppingDto));
                salesList.add(sales);
            }
        }
        salesService.insertSales(salesList);

        basketItemDtoList.clear();
        basketItemDtoList = new ArrayList<>();
        return salesList.stream().map(mappingService::salesEntityToDTO).toList();
    }

    public ResultBasketDto clearBasket() {
        basketItemDtoList.clear();
        basketItemDtoList = new ArrayList<>();
        return new ResultBasketDto();
    }

    public ResultBasketDto deleteFromBasket(Long id) {
        List<BasketItemDto> deletedItems = basketItemDtoList.stream().filter(item -> item.getCoffeeDto().getId() == id).toList();
        basketItemDtoList.removeAll(deletedItems);
        return applyDiscount(basketItemDtoList);
    }
}
