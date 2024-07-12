package com.swd392.group2.kgrill_service.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.swd392.group2.kgrill_service.dto.RevenueElementResponse;
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
@Schema(description = "ShipperInfo object response for admin panel")
public class ShipperInfoResponse {

    @JsonProperty(value = "content", index = 1)
    List<ShipperInfoElement> content;

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
}
