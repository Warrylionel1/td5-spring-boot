package com.example.ingredientspring.entities.Stock;

import com.example.ingredientspring.entities.Ingredient.Unit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockValue {
    private Double quantity;
    private Unit unit;
}
