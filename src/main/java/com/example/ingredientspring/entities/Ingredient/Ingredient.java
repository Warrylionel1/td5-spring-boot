package com.example.ingredientspring.entities.Ingredient;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
}
