package com.swd392.group2.kgrill_service.dto;


import com.swd392.group2.kgrill_model.model.DeliveryOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPaging {

    private LocalDate orderDate;
    private List<DeliveryOrder> deliveryOrders;

}
