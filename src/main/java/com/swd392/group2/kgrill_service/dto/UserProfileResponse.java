package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private List<CustomUserProfile> content;

    @JsonProperty(value = "page_no", index = 1)
    private int pageNo;
    @JsonProperty(value = "page_size", index = 2)
    private int pageSize;
    @JsonProperty(value = "total_elements", index = 3)
    private long totalElements;
    @JsonProperty(value = "total_pages",index = 4)
    private int totalPages;
    @JsonProperty(value = "last", index = 5)
    private boolean last;
}
