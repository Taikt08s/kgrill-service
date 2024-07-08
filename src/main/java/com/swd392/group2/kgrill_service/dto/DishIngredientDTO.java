package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Dish's Ingredient")
public class DishIngredientDTO {
    @JsonProperty("ingredient_id")
    private Integer id;
    @Schema(description = "Ingredient name", example = "Tôm")
    @JsonProperty("ingredient_name")
    private String name;
}
