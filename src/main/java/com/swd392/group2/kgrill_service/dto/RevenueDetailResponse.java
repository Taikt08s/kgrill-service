package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery response pagination object for Revenue detail")
public class RevenueDetailResponse {

    @JsonProperty(value = "content", index = 1)
    List<RevenueDetailElementResponse> content;

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
