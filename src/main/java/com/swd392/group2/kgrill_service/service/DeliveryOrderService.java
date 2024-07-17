package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_service.dto.DeliveryLocationDTO;
import com.swd392.group2.kgrill_service.dto.mobiledto.OrderDetailAfterLoginRequest;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public interface DeliveryOrderService {

    ResponseEntity<Object> updateDeliveryOrderLocation (Long id, DeliveryLocationDTO deliveryLocationDTO);

    void addPackageToDeliveryOrder (UUID userId, int packageId, int quantity);

    void updateOrderDetail(int orderDetailId, int quantity);



    OrderDetailAfterLoginRequest getOrderDetailAfterLogin(UUID userId);

    ResponseEntity<Object> getNumberOfOrders();

    ResponseEntity<Object> getRevenueByPeriod(int pageNo, int pageSize, String sortBy, String sortDir, String period, LocalDate startDate);

    ResponseEntity<Object> getDeliveryOrderDetailByAdmin(int pageNo, int pageSize, String sortBy, String sortDir, String period, LocalDate startDate);

    ResponseEntity<Object> getDeliveryOrderDetailByShipperId(int pageNo, int pageSize, String sortBy, String sortDir, int shipperId);

    ResponseEntity<Object> getDeliveryOrderByStatus(int pageNo, int pageSize, String sortBy, String sortDir, String status);
}
