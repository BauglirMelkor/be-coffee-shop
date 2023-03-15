package com.store.becoffeestore.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.store.becoffeestore.dto.ToppingDto;
import com.store.becoffeestore.entity.Topping;
import com.store.becoffeestore.exception.ToppingNotFound;
import com.store.becoffeestore.repository.ToppingRepository;

@Service
public class ToppingService {

    Logger logger = LoggerFactory.getLogger(ToppingService.class);

    private ToppingRepository toppingRepository;
    private MappingService mappingService;

    public ToppingService(ToppingRepository toppingRepository, MappingService mappingService) {
        this.toppingRepository = toppingRepository;
        this.mappingService = mappingService;
    }

    public ToppingDto insertTopping(ToppingDto toppingDto) {
        Topping topping = mappingService.toppingDtoToEntity(toppingDto);
        toppingRepository.save(topping);
        logger.debug("Topping is inserted name: {}, id: {}", topping.getName(), topping.getId());
        return mappingService.toppingEntityToDTO(topping);
    }

    public ToppingDto getTopping(Long id) throws ToppingNotFound {
        Optional<Topping> toppingOptional = toppingRepository.findById(id);
        Topping topping = toppingOptional.orElseThrow(ToppingNotFound::new);
        return mappingService.toppingEntityToDTO(topping);
    }

    public List<ToppingDto> getToppingList(List<Long> id) throws ToppingNotFound {
        List<ToppingDto> toppingDtoList = toppingRepository.findAllByIdIn(id)
            .stream()
            .map(topping -> mappingService.toppingEntityToDTO(topping)).toList();
        if (toppingDtoList.isEmpty()) {
            throw new ToppingNotFound();
        }
        return toppingDtoList;
    }

    public ToppingDto updateTopping(ToppingDto toppingDto, Long id) throws ToppingNotFound {
        Optional<Topping> toppingOptional = toppingRepository.findById(id);
        final Topping topping = toppingOptional.orElseThrow(ToppingNotFound::new);
        topping.setName(toppingDto.getName());
        topping.setPrice(toppingDto.getPrice());
        final Topping toppingUpdated = toppingRepository.save(topping);
        logger.debug("Topping is updated name: {}, id: {}", topping.getName(), topping.getId());
        return mappingService.toppingEntityToDTO(toppingUpdated);
    }

    public List<ToppingDto> getAllToppings() {
        return toppingRepository.findAll()
            .stream()
            .map(topping -> mappingService.toppingEntityToDTO(topping))
            .toList();
    }

    public ToppingDto deleteTopping(Long id) throws ToppingNotFound {
        Optional<Topping> toppingOptional = toppingRepository.findById(id);
        Topping topping = toppingOptional.orElseThrow(ToppingNotFound::new);
        toppingRepository.delete(topping);
        logger.debug("Topping is deleted name: {}, id: {}", topping.getName(), topping.getId());
        return mappingService.toppingEntityToDTO(topping);
    }

}
