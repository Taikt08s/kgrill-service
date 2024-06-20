package com.swd392.group2.kgrill_service.service;


import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_service.dto.DeliveryLocationDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface DeliveryOrderService {

    ResponseEntity<Object> updateDeliveryOrderLocation (Long id, DeliveryLocationDTO deliveryLocationDTO);

    void addPackageToDeliveryOrder (UUID userId, int packageId, int quantity);
}
