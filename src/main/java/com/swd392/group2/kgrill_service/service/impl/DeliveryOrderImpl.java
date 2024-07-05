package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.*;
import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.repository.*;
import com.swd392.group2.kgrill_service.dto.*;
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

import javax.swing.text.html.parser.Entity;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    public ResponseEntity<Object> getRevenueByPeriod(int pageNo, int pageSize, String sortBy, String sortDir, String period, LocalDate startDate) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<DeliveryOrder> deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByDateIncludingCurrentDay(pageable, startDate);
        List<DeliveryOrder> deliveryOrders = deliveryOrderPage.getContent();
        Map<Object, List<DeliveryOrder>> groupedList = groupByPeriod(deliveryOrders, period);

        List<RevenueElementResponse> content = new ArrayList<>();

        for (Map.Entry<Object, List<DeliveryOrder>> map : groupedList.entrySet()) {
            RevenueElementResponse revenueElementResponse = new RevenueElementResponse();

//             Lấy ngày đặt hàng
            revenueElementResponse.setOrderDate(getDateTimeFormatter(map.getKey(), period));

            // Đếm tổng số đơn hàng trong danh sách
            revenueElementResponse.setTotalOrderNumber(map.getValue().size());

            // Đếm số đơn hàng đã hoàn thành (status = "Delivered")
            revenueElementResponse.setCompletedNumber((int) map.getValue().stream()
                    .filter(deliveryOrder -> deliveryOrder.getStatus().equalsIgnoreCase("Delivered"))
                    .count());

            // Đếm số đơn hàng bị hủy (status = "Cancelled")
            revenueElementResponse.setCancelledNumber((int) map.getValue().stream()
                    .filter(deliveryOrder -> deliveryOrder.getStatus().equalsIgnoreCase("Cancelled"))
                    .count());

            // Tính tổng doanh thu của tất cả các đơn hàng trong danh sách
            revenueElementResponse.setTotalRevenue(map.getValue().stream()
                    .map(DeliveryOrder -> {
                        if (DeliveryOrder.getStatus().equalsIgnoreCase("Delivered")) {
                            return DeliveryOrder.getOrderValue();
                        } else if (DeliveryOrder.getStatus().equalsIgnoreCase("Cancelled")) {
                            return -DeliveryOrder.getOrderValue();
                        } else {
                            return 0f;
                        }
                    }).reduce(0f, Float::sum));

            // Tính tổng doanh thu của các đơn hàng đã hoàn thành
            revenueElementResponse.setCompletedOrder(map.getValue().stream()
                    .filter(deliveryOrder -> deliveryOrder.getStatus().equalsIgnoreCase("Delivered"))
                    .map(DeliveryOrder::getOrderValue).reduce(0f, Float::sum));

            // Tính tổng doanh thu của các đơn hàng bị hủy
            revenueElementResponse.setCancelledOrder(map.getValue().stream()
                    .filter(deliveryOrder -> deliveryOrder.getStatus().equalsIgnoreCase("Cancelled"))
                    .map(DeliveryOrder::getOrderValue).reduce(0f, Float::sum));

            content.add(revenueElementResponse);
        }

        if (sortDir.equalsIgnoreCase("asc")) {
            content.sort(Comparator.comparing(RevenueElementResponse::getOrderDate).reversed());
        } else {
            content.sort(Comparator.comparing(RevenueElementResponse::getOrderDate));
        }

        RevenueResponse revenueResponse = new RevenueResponse();
        revenueResponse.setContent(content);
        revenueResponse.setPageNo(pageable.getPageNumber());
        revenueResponse.setPageSize(pageable.getPageSize());
        revenueResponse.setTotalElements(deliveryOrderPage.getTotalElements());
        revenueResponse.setTotalPages(deliveryOrderPage.getTotalPages());
        revenueResponse.setLast(deliveryOrderPage.isLast());

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved revenue", revenueResponse);
    }

    @Override
    public ResponseEntity<Object> getRevenueDetailByPeriod(int pageNo, int pageSize, String sortBy, String sortDir, String period, LocalDate startDate) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<DeliveryOrder> deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByDateIncludingCurrentDay(pageable, startDate);

//        if (period.equalsIgnoreCase("yearly")) {
//            deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByYearly(pageable);
//        } else if (period.equalsIgnoreCase("monthly")) {
//            deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByMonthly(pageable);
//        }

        List<DeliveryOrder> deliveryOrders = deliveryOrderPage.getContent();
        List<RevenueDetailResponse> content = deliveryOrders.stream()
                .map(deliveryOrder -> modelMapper.map(deliveryOrder, RevenueDetailResponse.class))
                .collect(Collectors.toList());

        float totalPrice = 0;
        float completedOrder = 0;
        float cancelledOrder = 0;
        //Lặp qua từng delivery order để lấy ra các package name
        for (RevenueDetailResponse d : content) {
//            //Lấy ra các package name, total price
//            List<String> packageName = new ArrayList<>();
//            float deliveryOrderPrice = 0;
//            List<OrderDetail> orderDetails = orderDetailRepository.findByDeliveryOrderId(d.getId());
//            for (OrderDetail o : orderDetails) {
//                if (Objects.equals(o.getOrder().getId(), d.getId())) {
//                    deliveryOrderPrice += o.getComboPrice() * o.getQuantity();
//                    packageName.add(o.getPackageEntity().getName());
//                }
//            }
//            d.setOrderValue(deliveryOrderPrice);
//            d.setPackageName(packageName);
//
//            //Lấy Username
//            User users = userRepository.findById(d.getAccountId())
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//            d.setUserName(users.getFirstName() + " " + users.getLastName());
//
//            //Lấy ShipperName
//            Shipper shipper = shipperRepository.findById((long) d.getShipperId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Shipper", "id", String.valueOf(d.getShipperId())));
//            User user = userRepository.findById(UUID.fromString(shipper.getUuid()))
//                    .orElseThrow(() -> new ResourceNotFoundException("Shipper", "id", shipper.getUuid()));
//            d.setShipperName(user.getFirstName() + " " + user.getLastName());
//
//            totalPrice += deliveryOrderPrice;
//
//            //Tính completed order và cancelled order
//            if (d.getStatus().equals("Cancelled")) {
//                List<OrderDetail> orders = orderDetailRepository.findByDeliveryOrderId(d.getId());
//                for (OrderDetail o : orders) {
//                    if (Objects.equals(o.getOrder().getId(), d.getId())) {
//                        cancelledOrder += o.getComboPrice() * o.getQuantity();
//                    }
//                }
//            }

            completedOrder = totalPrice - cancelledOrder;
        }


        RevenueResponse revenueResponse = new RevenueResponse();
//        revenueResponse.setContent(content);
//        revenueResponse.setPageNo(pageable.getPageNumber());
//        revenueResponse.setPageSize(pageable.getPageSize());
//        revenueResponse.setTotalElements(deliveryOrderPage.getTotalElements());
//        revenueResponse.setTotalPages(deliveryOrderPage.getTotalPages());
//        revenueResponse.setLast(deliveryOrderPage.isLast());
//        revenueResponse.setTotalRevenue(totalPrice);
//        revenueResponse.setCompletedOrder(completedOrder);
//        revenueResponse.setCancelledOrder(cancelledOrder);

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved revenue by daily", revenueResponse);

    }

    public Map<Object, List<DeliveryOrder>> groupByPeriod(List<DeliveryOrder> deliveryOrders, String period) {

        Map<Object, List<DeliveryOrder>> groupedList = deliveryOrders.stream()
                .collect(Collectors.groupingBy(deliveryOrder ->
                        LocalDate.ofInstant(deliveryOrder.getOrderDate().toInstant(), ZoneId.systemDefault())
                ));

        if (period.equalsIgnoreCase("monthly")) {
            groupedList = deliveryOrders.stream()
                    .collect(Collectors.groupingBy(deliveryOrder ->
                            YearMonth.from(deliveryOrder.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    ));
        } else if (period.equalsIgnoreCase("yearly")) {
            groupedList = deliveryOrders.stream()
                    .collect(Collectors.groupingBy(deliveryOrder ->
                            Year.from(deliveryOrder.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    ));
        }

        return groupedList;
    }

    public String getDateTimeFormatter(Object mapKey, String period){
        String date;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // đặt lại formatter dựa trên period
        if ("monthly".equalsIgnoreCase(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else if ("yearly".equalsIgnoreCase(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }

        if (mapKey instanceof LocalDate) {
            date = ((LocalDate) mapKey).format(formatter);
        } else if (mapKey instanceof YearMonth) {
            date = ((YearMonth) mapKey).format(formatter);
        } else if (mapKey instanceof Year) {
            date = ((Year) mapKey).toString();
        } else {
            date = mapKey.toString();
        }

        return date;
    }
}
