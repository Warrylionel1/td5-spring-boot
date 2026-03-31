package com.example.ingredientspring.service;

import com.example.ingredientspring.entities.Dish.Dish;
import com.example.ingredientspring.entities.Ingredient.Ingredient;
import com.example.ingredientspring.exception.ResourceNotFoundException;
import com.example.ingredientspring.repository.DishRepository;
import com.example.ingredientspring.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Dish findById(Integer dishId) {
        Dish dish = dishRepository.findById(dishId).orElse(null);
        if (dish == null) {
            throw new ResourceNotFoundException("Dish with id " + dishId + " not found");
        }
        return dish;
    }

    public Dish attachAndDetach(Integer dishId, List<Ingredient> ingredients) throws BadRequestException {
        if (ingredients.isEmpty()) {
            throw new BadRequestException("You need to specify at least one ingredient");
        }
        Dish dish = dishRepository.findById(dishId).orElse(null);
        if (dish == null) {
            throw new ResourceNotFoundException("Dish with id " + dishId + " not found");
        }
        for (Ingredient ingredient : ingredients) {
            Ingredient isExisting = this.ingredientRepository.findById(ingredient.getId()).orElse(null);

            if (isExisting != null) {
                if (dish.getIngredients().stream().map(Ingredient::getId).toList().contains(ingredient.getId())) {
                    this.dishRepository.detachIngredient(dishId, ingredient.getId());
                } else  {
                    this.dishRepository.attach(dishId, ingredient.getId());
                }
            }
        }
        return this.dishRepository.findById(dishId).orElse(null);
    }

    public List<Ingredient> getDishIngredients(Integer dishId, String ingredientName, Double ingredientPriceAround) {
        dishRepository.findById(dishId).orElseThrow(() -> new ResourceNotFoundException("Dish with id " + dishId + " not found"));
        return dishRepository.findIngredientByDishId(dishId, ingredientName, ingredientPriceAround);
    }
}
