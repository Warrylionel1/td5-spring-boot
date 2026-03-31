package com.example.ingredientspring.repository;

import com.example.ingredientspring.entities.Dish.Dish;
import com.example.ingredientspring.entities.Dish.DishTypeEnum;
import com.example.ingredientspring.entities.Ingredient.Ingredient;
import com.example.ingredientspring.entities.Ingredient.Unit;
import org.springframework.stereotype.Repository;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class DishRepository {
    private final DataSource dataSource;
    private final IngredientRepository ingredientRepository;

    public DishRepository(DataSource dataSource, IngredientRepository ingredientRepository) {
        this.dataSource = dataSource;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String query = """
                SELECT id        AS dish_id,
                       name      AS dish_name,
                       dish_type,
                       price     AS dish_price
                FROM dish
                ORDER BY id
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dish.setName(rs.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getObject("dish_price") == null
                        ? null : rs.getDouble("dish_price"));
                dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));

                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    public Optional<Dish> findById(Integer id) {
        String query = """
                SELECT id        AS dish_id,
                       name      AS dish_name,
                       dish_type,
                       price     AS dish_price
                FROM dish
                WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dish.setName(rs.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getObject("dish_price") == null
                        ? null : rs.getDouble("dish_price"));
                dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));

                return Optional.of(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void detachIngredient(Integer dishId, Integer ingredientId) {
        String query = """
                DELETE FROM dishingredients
                WHERE id_dish = ?
                  AND id_ingredient = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, dishId);
            ps.setInt(2, ingredientId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void attach(Integer dishId, Integer ingredientId) {
        String query = """
                INSERT INTO dishingredients (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?::unit_type)
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, dishId);
            ps.setInt(2, ingredientId);
            ps.setDouble(3, 1);
            ps.setString(4, Unit.KG.toString());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateIngredients(Integer dishId, List<Ingredient> newIngredients) {
        List<Ingredient> current = ingredientRepository.findByDishId(dishId);

        for (Ingredient old : current) {
            boolean stillPresent = newIngredients.stream()
                    .anyMatch(i -> i.getId().equals(old.getId()));
            if (!stillPresent) {
                detachIngredient(dishId, old.getId());
            }
        }

        for (Ingredient newIng : newIngredients) {
            ingredientRepository.findById(newIng.getId()).ifPresent(existing -> {
                boolean alreadyLinked = current.stream()
                        .anyMatch(i -> i.getId().equals(existing.getId()));
                if (!alreadyLinked) {
                    attach(dishId, existing.getId());
                }
            });
        }
    }
}
