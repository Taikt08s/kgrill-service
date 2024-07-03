package com.swd392.group2.kgrill_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Schema(description = "Request object for Package")
public class PackageRequest {

    @JsonIgnore
    @JsonProperty("package_id")
    private Integer id;

    @Schema(description = "Package's name", example = "Combo Bò nướng mĩ vị Tailor")
    @JsonProperty("package_name")
    private String name;

    @Schema(description = "Package's price", example = "999000")
    @JsonProperty("package_price")
    private Float price;

    @Schema(description = "Package's description", example = "Combo Bò nướng mĩ vị Tailor là combo phù hợp với ...")
    @JsonProperty("package_description")
    private String description;

    @Schema(description = "Package's type", example = "lẩu")
    @JsonProperty("package_type")
    private String packageType;

    @Schema(description = "Package's size", example = "1 - 2 người")
    @JsonProperty("package_size")
    private String packageSize;

    @Schema(description = "Package's active status", example = "inactive")
    @JsonProperty("package_active_status")
    private boolean active;

    @JsonProperty("package_thumbnail")
    private String thumbnail;
}
