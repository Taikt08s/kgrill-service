package com.swd392.group2.kgrill_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ShipperInfo element object response for shipperInfoResponse")
public class ShipperInfoElement {

    @Schema(description = "Shipper's ID", example = "1")
    private int id;

    @Schema(description = "Shipper's UUID", example = "1")
    private String uuid;

    @Schema(description = "Shipper's name", example = "Nguyễn Văn A")
    private String name;

    @Schema(description = "Shipper's total order", example = "20")
    @JsonProperty(value = "total_order")
    private int totalOrder;

    @Schema(description = "Shipper's completed order", example = "10")
    @JsonProperty(value = "completed_order")
    private int completedOrder;

    @Schema(description = "Shipper's cancelled order", example = "10")
    @JsonProperty(value = "cancelled_order")
    private int cancelledOrder;
}
