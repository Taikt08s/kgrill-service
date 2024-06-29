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
@Schema(description = "Response object for Dish to show in Dish list")
public class IngredientDTO {
    @JsonProperty("ingredient_id")
    private Integer id;
    @Schema(description = "Ingredient's name", example = "Ngũ vị hương")
    @JsonProperty("ingredient_name")
    private String name;
}
