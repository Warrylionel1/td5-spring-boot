package com.example.ingredientspring.entities.Stock;

import lombok.*;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class StockMovement {
    private Integer id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;
}
