package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.DeliveryLocationDTO;
import org.springframework.http.ResponseEntity;

public interface DeliveryOrderService {
    ResponseEntity<Object> updateDeliveryOrderLocation (Long id, DeliveryLocationDTO deliveryLocationDTO);
}
