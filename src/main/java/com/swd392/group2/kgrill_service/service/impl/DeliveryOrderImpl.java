package com.swd392.group2.kgrill_service.service.impl;

import com.group2.kgrill.dto.DeliveryLocationDTO;
import com.group2.kgrill.exception.CustomSuccessHandler;
import com.group2.kgrill.exception.ExceptionResponse;
import com.swd392.group2.kgrill_model.model.DeliveryOrder;
import com.swd392.group2.kgrill_model.repository.DeliveryOrderRepository;
import com.swd392.group2.kgrill_service.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DeliveryOrderImpl implements DeliveryOrderService {

    private final DeliveryOrderRepository deliveryOrderRepository;

    @Override
    public ResponseEntity<Object> updateDeliveryOrderLocation(Long id, DeliveryLocationDTO deliveryLocationDTO) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
                .orElse(null);
        if (deliveryOrder == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No delivery order found");
        }

        deliveryOrder.setShippedAddress(deliveryLocationDTO.getAddress());
        deliveryOrder.setLatitude(deliveryLocationDTO.getLatitude());
        deliveryOrder.setLongitude(deliveryLocationDTO.getLongitude());

        deliveryOrderRepository.save(deliveryOrder);
        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully updated delivery order location", deliveryOrder);
    }

}
