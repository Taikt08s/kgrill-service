package com.swd392.group2.kgrill_service.dto.mobiledto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Order to check out")
public class DeliveryOrderDtoForCheckOut {

    @JsonProperty("delivery_order_id")
    private Integer orderId;

    @JsonProperty("order_value")
    @Schema(description = "Order's value", example = "2000000")
    private Long orderValue;

    @JsonProperty("shipped_address")
    @Schema(description = "Shipped address", example = "Duong so 2, Tang Nhon Phu A,....")
    private String shippedAddress;

    @JsonProperty("order_latitude")
    @Schema(description = "Order's address latitude")
    private Double latitude;

    @JsonProperty("order_longitude")
    @Schema(description = "Order's address longitude")
    private Double longitude;

    @Schema(description = "Shipping fee", example = "20000")
    @JsonProperty("shipping_fee")
    private Long shippingFee;

    @Schema(description = "Payment method", example = "Cash")
    @JsonProperty("order_payment_method")
    private String paymentMethod;
}
