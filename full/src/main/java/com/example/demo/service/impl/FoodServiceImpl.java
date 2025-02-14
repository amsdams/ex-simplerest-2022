package com.example.demo.service.impl;

import com.example.demo.domain.Food;
import com.example.demo.repository.FoodRepository;
import com.example.demo.service.FoodService;
import com.example.demo.service.dto.FoodDTO;
import com.example.demo.service.mapper.FoodMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Food}.
 */
@Service
@Transactional
public class FoodServiceImpl implements FoodService {

    private final Logger log = LoggerFactory.getLogger(FoodServiceImpl.class);

    private final FoodRepository foodRepository;

    private final FoodMapper foodMapper;

    public FoodServiceImpl(FoodRepository foodRepository, FoodMapper foodMapper) {
        this.foodRepository = foodRepository;
        this.foodMapper = foodMapper;
    }

    @Override
    public FoodDTO save(FoodDTO foodDTO) {
        log.debug("Request to save Food : {}", foodDTO);
        Food food = foodMapper.toEntity(foodDTO);
        food = foodRepository.save(food);
        return foodMapper.toDto(food);
    }

    @Override
    public Optional<FoodDTO> partialUpdate(FoodDTO foodDTO) {
        log.debug("Request to partially update Food : {}", foodDTO);

        return foodRepository
                .findById(foodDTO.getId())
                .map(existingFood -> {
                    foodMapper.partialUpdate(existingFood, foodDTO);

                    return existingFood;
                })
                .map(foodRepository::save)
                .map(foodMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodDTO> findAll() {
        log.debug("Request to get all Foods");
        return foodRepository.findAll().stream().map(foodMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FoodDTO> findOne(Long id) {
        log.debug("Request to get Food : {}", id);
        return foodRepository.findById(id).map(foodMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Food : {}", id);
        foodRepository.deleteById(id);
    }
}
