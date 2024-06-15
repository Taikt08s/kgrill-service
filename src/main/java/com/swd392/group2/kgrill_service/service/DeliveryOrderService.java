package com.swd392.group2.kgrill_service.service;
import com.group2.kgrill.dto.DeliveryLocationDTO;
import org.springframework.http.ResponseEntity;

public interface DeliveryOrderService {
    ResponseEntity<Object> updateDeliveryOrderLocation (Long id, DeliveryLocationDTO deliveryLocationDTO);
}
