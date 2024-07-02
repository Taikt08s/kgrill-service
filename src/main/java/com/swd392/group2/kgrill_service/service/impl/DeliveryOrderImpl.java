package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.*;
import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.repository.*;
import com.swd392.group2.kgrill_service.dto.DeliveryLocationDTO;
import com.swd392.group2.kgrill_service.dto.DeliveryResponseForRevenue;
import com.swd392.group2.kgrill_service.dto.OrderCountResponse;
import com.swd392.group2.kgrill_service.dto.RevenueResponse;
import com.swd392.group2.kgrill_service.exception.CustomSuccessHandler;
import com.swd392.group2.kgrill_service.exception.ResourceNotFoundException;
import com.swd392.group2.kgrill_service.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DeliveryOrderImpl implements DeliveryOrderService {

    private final DeliveryOrderRepository deliveryOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PackageRepository packageRepository;
    private final ShipperRepository shipperRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public ResponseEntity<Object> getNumberOfOrders() {

        OrderCountResponse orderCountResponse = new OrderCountResponse();

        int dailyOrder = deliveryOrderRepository.countByDaily();
        int weeklyOrder = deliveryOrderRepository.countByWeekly();
        int monthlyOrder = deliveryOrderRepository.countByMonthly();
        int yearlyOrder = deliveryOrderRepository.countByYearly();

        orderCountResponse.setDailyOrder(dailyOrder);
        orderCountResponse.setWeeklyOrder(weeklyOrder);
        orderCountResponse.setMonthlyOrder(monthlyOrder);
        orderCountResponse.setYearlyOrder(yearlyOrder);

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved order count", orderCountResponse);
    }

    @Override
    public ResponseEntity<Object> getRevenueByDaily(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<DeliveryOrder> deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByYearly(pageable);
        List<DeliveryOrder> deliveryOrders = deliveryOrderPage.getContent();
        List<DeliveryResponseForRevenue> content = deliveryOrders.stream()
                .map(deliveryOrder -> modelMapper.map(deliveryOrder, DeliveryResponseForRevenue.class))
                .collect(Collectors.toList());

        float totalPrice = 0;
        float completedOrder = 0;
        float cancelledOrder = 0;
        //Lặp qua từng delivery order để lấy ra các package name
        for (DeliveryResponseForRevenue d : content) {
            //Lấy ra các package name, total price
            List<String> packageName = new ArrayList<>();
            float deliveryOrderPrice = 0;
            List<OrderDetail> orderDetails = orderDetailRepository.findByDeliveryOrderId(d.getId());
            for (OrderDetail o : orderDetails) {
                if (Objects.equals(o.getOrder().getId(), d.getId())) {
                    deliveryOrderPrice += o.getComboPrice() * o.getQuantity();
                    packageName.add(o.getPackageEntity().getName());
                }
            }
            d.setOrderValue(deliveryOrderPrice);
            d.setPackageName(packageName);

            //Lấy Username
            User users = userRepository.findById(d.getAccountId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            d.setUserName(users.getFirstName() + " " + users.getLastName());

            //Lấy ShipperName
            Shipper shipper = shipperRepository.findById((long) d.getShipperId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipper", "id", String.valueOf(d.getShipperId())));
            User user = userRepository.findById(UUID.fromString(shipper.getUuid()))
                    .orElseThrow(() -> new ResourceNotFoundException("Shipper", "id", shipper.getUuid()));
            d.setShipperName(user.getFirstName() + " " + user.getLastName());

            totalPrice += deliveryOrderPrice;

            //Tính completed order và cancelled order
           if (d.getStatus().equals("Cancelled")) {
                List<OrderDetail> orders = orderDetailRepository.findByDeliveryOrderId(d.getId());
                for (OrderDetail o : orders) {
                    if (Objects.equals(o.getOrder().getId(), d.getId())) {
                        cancelledOrder += o.getComboPrice() * o.getQuantity();
                    }
                }
           }

           completedOrder = totalPrice - cancelledOrder;
        }

        RevenueResponse revenueResponse = new RevenueResponse();
        revenueResponse.setContent(content);
        revenueResponse.setPageNo(pageable.getPageNumber());
        revenueResponse.setPageSize(pageable.getPageSize());
        revenueResponse.setTotalElements(deliveryOrderPage.getTotalElements());
        revenueResponse.setTotalPages(deliveryOrderPage.getTotalPages());
        revenueResponse.setLast(deliveryOrderPage.isLast());
        revenueResponse.setTotalRevenue(totalPrice);
        revenueResponse.setCompletedOrder(completedOrder);
        revenueResponse.setCancelledOrder(cancelledOrder);

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved revenue by daily", revenueResponse);
    }

}
