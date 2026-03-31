package com.example.ingredientspring.entities.Dish;

import com.example.ingredientspring.entities.Ingredient.Ingredient;
import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<Ingredient> ingredients;
}
