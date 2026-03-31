package com.example.ingredientspring.repository;

import com.example.ingredientspring.entities.Ingredient.CategoryEnum;
import com.example.ingredientspring.entities.Ingredient.Ingredient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class IngredientRepository {
    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = """
                SELECT id, name, price, category
                FROM ingredient
                """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredients.add(ingredient);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }

    public Optional<Ingredient> findById(Integer id) {
        String query = """
                SELECT id, name, price, category
                FROM ingredient
                WHERE id = ?
                """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                return Optional.of(ingredient);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Ingredient> findByDishId(Integer dishId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = """
                SELECT i.id, i.name, i.price, i.category
                FROM ingredient i
                JOIN dish_ingredient di ON i.id = di.id_ingredient
                WHERE di.id_dish = ?
                """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredients.add(ingredient);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }

    public double computeStockAt(Integer ingredientId, String at, String unit) {
        String query = """
                SELECT COALESCE(SUM(
                    CASE WHEN type = 'IN'  THEN  quantity
                         WHEN type = 'OUT' THEN -quantity
                         ELSE 0
                    END
                ), 0) AS stock_value
                FROM stock_movement
                WHERE ingredient_id = ?
                  AND unit = ?
                  AND created_at <= ?::timestamp
                """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, ingredientId);
            stmt.setString(2, unit);
            stmt.setString(3, at);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("stock_value");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }
}


