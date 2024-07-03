package com.swd392.group2.kgrill_service.dto.mobiledto;

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
@Schema(description = "Response object for Package's dish")
public class PackageDishDtoOnMobile {

    @Schema(description = "Dish's name", example = "Thịt bò Tailor Sweet")
    @JsonProperty("dish_name")
    private String dishName;

    @Schema(description = "Dish's price", example = "69000")
    @JsonProperty("dish_price")
    private Float dishPrice;

    @Schema(description = "Dish quantity", example = "1 phần")
    @JsonProperty("dish_quantity")
    private Integer quantity;

}
