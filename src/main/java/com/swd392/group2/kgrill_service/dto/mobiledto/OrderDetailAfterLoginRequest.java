package com.swd392.group2.kgrill_service.dto.mobiledto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Response object for cart detail of current user account")
public class OrderDetailAfterLoginRequest {

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("order_detail_list")
    private List<OrderDetailDto> orderDetailList;
}
