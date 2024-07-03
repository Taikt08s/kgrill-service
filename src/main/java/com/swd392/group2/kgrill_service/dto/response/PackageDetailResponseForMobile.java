package com.swd392.group2.kgrill_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swd392.group2.kgrill_service.dto.mobiledto.PackageDishDtoOnMobile;
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
@Schema(description = "Response object for Package's detail to show on mobile")
public class PackageDetailResponseForMobile {

    @JsonProperty("package_id")
    private Integer id;

    @Schema(description = "Package's name", example = "Combo Bò nướng mĩ vị Tailor")
    @JsonProperty("package_name")
    private String name;

    @Schema(description = "Package's description", example = "Combo Bò nướng mĩ vị Tailor là combo phù hợp với ...")
    @JsonProperty("package_code")
    private String description;

    @Schema(description = "Package's price", example = "999000")
    @JsonProperty("package_price")
    private Float price;

    @Schema(description = "Package's type", example = "lẩu")
    @JsonProperty("package_type")
    private String packageType;

    @Schema(description = "Package's size", example = "1 - 2 người")
    @JsonProperty("package_size")
    private String packageSize;

    @Schema(description = "Package's thumbnail url")
    @JsonProperty("package_thumbnail")
    private String thumbnail;

    @Schema(description = "Package's dishes")
    @JsonProperty("dishes_of_package")
    private List<PackageDishDtoOnMobile> packageDishes;
}
