package com.swd392.group2.kgrill_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DeliveryOrderElement", description = "Delivery Order element that return user ordering for Manager")
public class DeliveryOrderElement {

    @Schema(name = "id", description = "Delivery Order ID")
    @JsonProperty(value = "Delivery_Order_Id", index = 1)
    private Integer id;

    @JsonIgnore
    private UUID accountId;

    @Schema(name = "User's ame", description = "Customer Name")
    @JsonProperty(value = "User_name", index = 2)
    private String userName;

    @Schema(name = "user's Phone", description = "Customer Phone")
    @JsonProperty(value = "Phone", index = 3)
    private String Phone;

    @JsonProperty(value = "Package_name", index = 4)
    @Schema(description = "Package's name", example = "Combo Bò nướng mĩ vị Tailor")
    private List<String> packageName;

    @Schema(name = "orderValue", description = "Total Price")
    @JsonProperty(value = "Order_value", index = 5)
    private float orderValue;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    @JsonProperty(value = "Address", index = 6)
    private String address;

    @Schema(description = "Latitude of the address", example = "21.123456")
    @JsonProperty(value = "Latitude", index = 7)
    private Double latitude;

    @Schema(description = "Longitude of the address", example = "105.123456")
    @JsonProperty(value = "Longitude", index = 8)
    private Double longitude;

    @Schema(name = "orderDate", description = "Order Date")
    @JsonProperty(value = "Order_date" , index = 9)
    private String orderDate;

    @Schema(name = "orderStatus", description = "Order Status")
    @JsonProperty(value = "Order_status", index = 10)
    private String orderStatus;


}
