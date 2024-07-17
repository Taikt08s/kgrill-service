package com.swd392.group2.kgrill_service.dto.mobiledto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swd392.group2.kgrill_model.model.OrderDetail;
import com.swd392.group2.kgrill_model.model.PaymentMethod;
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
@Schema(description = "Response object for order to show in order history")
public class DeliveryOrderDto {

    @JsonProperty("delivery_order_id")
    private Integer orderId;

    @JsonProperty("order_date")
    private Date orderDate;

    @JsonProperty("order_value")
    private Long orderValue;

    @JsonProperty("shipper_date")
    private Date shippedDate;

    @JsonProperty("shipped_address")
    private String shippedAddress;

    @JsonProperty("shipping_fee")
    private Long shippingFee;

    @JsonProperty("order_status")
    private String status;

    @JsonProperty("order_payment_method")
    private String paymentMethod;

    @JsonProperty("order_detail")
    private List<OrderDetailDto> orderDetails;
}
