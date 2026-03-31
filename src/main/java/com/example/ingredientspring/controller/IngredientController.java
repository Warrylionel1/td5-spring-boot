package com.example.ingredientspring.controller;

import com.example.ingredientspring.entities.Ingredient.Ingredient;
import com.example.ingredientspring.entities.Ingredient.Unit;
import com.example.ingredientspring.exception.ResourceNotFoundException;
import com.example.ingredientspring.service.IngredientService;
import com.example.ingredientspring.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;
    private final StockService stockService;

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> findIngredientStock (@PathVariable Integer id,
                                                  @RequestParam (name = "at", required = false) Instant at,
                                                  @RequestParam (name = "unit", required = false) Unit unit)
    {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(this.stockService.findIngredientStock(id, at, unit));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable Integer id)
    {
        try {
            Ingredient ingredient = this.ingredientService.findById(id);
            return ResponseEntity.status(HttpStatus.OK).body(ingredient);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("")
    public  ResponseEntity<?> findAll()
    {
        try {
            List<Ingredient> ingredients = this.ingredientService.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(ingredients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
