package com.example.ingredientspring.service;

import com.example.ingredientspring.entities.Ingredient.Ingredient;
import com.example.ingredientspring.entities.Ingredient.Unit;
import com.example.ingredientspring.entities.Stock.StockValue;
import com.example.ingredientspring.exception.ResourceNotFoundException;
import com.example.ingredientspring.repository.IngredientRepository;
import com.example.ingredientspring.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockMovementRepository stockMovementRepository;
    private final IngredientRepository ingredientRepository;

    public StockValue findIngredientStock(Integer id, Instant at, Unit unit) throws BadRequestException {

        if (at == null || unit == null) {
            throw new BadRequestException(
                    "Either mandatory query parameter `at` or `unit` is not provided."
            );
        }
        ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ingredient.id=" + id + " is not found")
                );
        return stockMovementRepository.findByIngredientId(id, at, unit);
    }
}