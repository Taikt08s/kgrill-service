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
@Schema(description = "Response object for Order Detail to show in OrderDetailAfterLoginRequest")
public class OrderDetailDto {

    @JsonProperty("order_detail_id")
    private Integer orderDetailId;

    @JsonProperty("package_id")
    private Integer packageId;

    @Schema(description = "Package's name", example = "Combo Bò nướng mĩ vị")
    @JsonProperty("package_name")
    private String packageName;

    @Schema(description = "Package's type", example = "nướng")
    @JsonProperty("package_type")
    private String packageType;

    @Schema(description = "Package's size", example = "1-2 người")
    @JsonProperty("package_size")
    private String packageSize;

    @Schema(description = "Package's quantity", example = "1")
    @JsonProperty("package_quantity")
    private int packageQuantity;

    @Schema(description = "Package's price", example = "999000")
    @JsonProperty("package_price")
    private Long packagePrice;

    @JsonProperty("package_thumbnail_url")
    private String thumbnailUrl;
}
