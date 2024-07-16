package com.swd392.group2.kgrill_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DeliveryOrderForManager", description = "Delivery Order object that pagination DeliveryOrderElement")
public class DeliveryOrderForManager {

    @JsonProperty(value = "content", index = 1)
    List<DeliveryOrderElement> content;

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
