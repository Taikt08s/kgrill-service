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
@Schema(description = "Request object for Package's Dish")
public class PackageDishDto {

    @JsonProperty("dish_id")
    private Integer id;

    @Schema(description = "Dish name", example = "1 phan bo Tailor")
    @JsonProperty("dish_name")
    private String name;

    @Schema(description = "Dish quantity", example = "1")
    @JsonProperty("dish_quantity")
    private Integer quantity;
}
