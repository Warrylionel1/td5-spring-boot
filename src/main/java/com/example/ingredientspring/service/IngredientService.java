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
        return ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ingredient.id=" + id + " is not found")
                );
    }
}
