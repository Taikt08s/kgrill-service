package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.DeliveryOrder;
import com.swd392.group2.kgrill_model.model.OrderDetail;
import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.model.User;
import com.swd392.group2.kgrill_model.repository.DeliveryOrderRepository;
import com.swd392.group2.kgrill_model.repository.OrderDetailRepository;
import com.swd392.group2.kgrill_model.repository.PackageRepository;
import com.swd392.group2.kgrill_model.repository.UserRepository;
import com.swd392.group2.kgrill_service.dto.DeliveryLocationDTO;
import com.swd392.group2.kgrill_service.exception.CustomSuccessHandler;
import com.swd392.group2.kgrill_service.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DeliveryOrderImpl implements DeliveryOrderService {

    private final DeliveryOrderRepository deliveryOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

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

    @Override
    public void addPackageToDeliveryOrder(UUID userId, int packageId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Package pkg = packageRepository.findById(packageId).orElseThrow(() -> new RuntimeException("Package not found"));
        if (user.getCurrentOrder() == null) {
            DeliveryOrder newOrder = new DeliveryOrder();
            newOrder.setAccount(user);
            newOrder.setStatus("ordering");
            user.setCurrentOrder(newOrder);
            user = userRepository.save(user);
        }
        OrderDetail newOrderDetail = new OrderDetail();
        newOrderDetail.setQuantity(quantity); // check quantity or not?
        newOrderDetail.setPackageEntity(pkg);
        newOrderDetail.setComboPrice(pkg.getPrice());
        newOrderDetail.setOrder(user.getCurrentOrder());
        orderDetailRepository.save(newOrderDetail);
    }

}
