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
@Schema(description = "Response object for Category to show in Category list")
public class CategoryDTO {
    @JsonProperty("category_id")
    private Integer id;
    @Schema(description = "Dish's category", example = "Dĩa nấm")
    @JsonProperty("category_name")
    private String category;
}
