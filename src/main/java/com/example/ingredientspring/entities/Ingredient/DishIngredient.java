package com.example.ingredientspring.entities.Ingredient;

import com.example.ingredientspring.entities.Dish.Dish;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DishIngredient {
    private int id;
    private Dish dish;
    private Ingredient ingredient;
    private double quantity;
    private Unit unit;
}
