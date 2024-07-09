package com.swd392.group2.kgrill_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swd392.group2.kgrill_model.model.DishCategory;
import com.swd392.group2.kgrill_service.dto.CategoryDTO;
import com.swd392.group2.kgrill_service.dto.DishIngredientDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Dish")
public class DishRequest {
    @JsonProperty("dish_id")
    private Integer id;
    @Schema(description = "Dish's name", example = "Combo Bò nướng")
    @JsonProperty("dish_name")
    private String name;
    @Schema(description = "Dish's price", example = "99000")
    @JsonProperty("dish_price")
    private Float price;
    @Schema(description = "Dish's category")
    @JsonProperty("dish_category")
    private CategoryDTO category;
    @Schema(description = "Dish's ingredient list")
    @JsonProperty("dish_ingredient_list")
    private List<DishIngredientDTO> dishIngredientList;

}
