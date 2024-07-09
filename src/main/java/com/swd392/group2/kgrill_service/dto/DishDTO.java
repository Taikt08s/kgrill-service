package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Response object for Dish to show in Dish list")
public class DishDTO {
    @JsonProperty("dish_id")
    private Integer id;
    @Schema(description = "Dish's name", example = "Bò Nam Phi")
    @JsonProperty("dish_name")
    private String name;
    @Schema(description = "Dish's price", example = "99000")
    @JsonProperty("dish_price")
    private Float price;
}
