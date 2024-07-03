package com.swd392.group2.kgrill_service.dto;


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
@Schema(description = "Response object for Revenue")
public class RevenueResponse {

    @JsonProperty(value = "content", index = 1)
    List<DeliveryResponseForRevenue> content;

    @JsonProperty(value = "page_no", index = 2)
    private int pageNo;
    @JsonProperty(value = "page_size", index = 3)
    private int pageSize;
    @JsonProperty(value = "total_elements", index = 4)
    private long totalElements;
    @JsonProperty(value = "total_pages",index = 5)
    private int totalPages;
    @JsonProperty(value = "last", index = 6)
    private boolean last;

    @JsonProperty(value = "total_revenue", index = 7)
    private float totalRevenue;
    @JsonProperty(value = "completed_order", index = 8)
    private float completedOrder;
    @JsonProperty(value = "cancelled_order", index = 9)
    private float cancelledOrder;


}
