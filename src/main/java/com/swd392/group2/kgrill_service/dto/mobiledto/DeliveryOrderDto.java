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
@Schema(description = "Response object for Order to show in Order History")
public class DeliveryOrderDto {

    @JsonProperty("delivery_order_id")
    private Integer orderId;

    @JsonProperty("order_date")
    private Date orderDate;

    @Schema(description = "Order's value", example = "2000000")
    @JsonProperty("order_value")
    private Long orderValue;

    @JsonProperty("shipper_date")
    private Date shippedDate;

    @Schema(description = "Shipped address", example = "Duong so 2, Tang Nhon Phu A,....")
    @JsonProperty("shipped_address")
    private String shippedAddress;

    @Schema(description = "User's phone number", example = "0965423786")
    @JsonProperty("user_phone")
    private String customerPhone;

    @Schema(description = "Shipping fee", example = "20000")
    @JsonProperty("shipping_fee")
    private Long shippingFee;

    @JsonProperty("order_status")
    private String status;

    @JsonProperty("order_payment_method")
    private String paymentMethod;

    @JsonProperty("order_detail")
    private List<OrderDetailDtoForOrderHistory> orderDetails;
}
