package com.example.ingredientspring.service;

import com.example.ingredientspring.entities.Ingredient.Ingredient;
import com.example.ingredientspring.exception.ResourceNotFoundException;
import com.example.ingredientspring.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient findById(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElse(null);
        if (ingredient.getId() == null) {
            throw new ResourceNotFoundException("Ingredient with id " + id + " not found");
        }

        return ingredient;
    }
}
