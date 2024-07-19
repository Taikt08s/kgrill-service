package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.*;
import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.repository.*;
import com.swd392.group2.kgrill_service.dto.*;
import com.swd392.group2.kgrill_service.dto.mobiledto.*;
import com.swd392.group2.kgrill_service.dto.response.DeliveryOrderElement;
import com.swd392.group2.kgrill_service.dto.response.DeliveryOrderForManager;
import com.swd392.group2.kgrill_service.exception.CustomSuccessHandler;
import com.swd392.group2.kgrill_service.exception.OrderDetailNotFoundException;
import com.swd392.group2.kgrill_service.exception.ResourceNotFoundException;
import com.swd392.group2.kgrill_service.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
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
    private final PaymentMethodRepository paymentMethodRepository;
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
    @Transactional
    public void addPackageToDeliveryOrder(UUID userId, int packageId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Package pkg = packageRepository.findById(packageId).orElseThrow(() -> new RuntimeException("Package not found"));
        if (user.getCurrentOrder() == null) {
            DeliveryOrder newOrder = new DeliveryOrder();
            newOrder.setAccount(user);
            newOrder.setStatus("Ordering");
            user.setCurrentOrder(newOrder);
            user = userRepository.save(user);
        }
        for (OrderDetail orderDetail : orderDetailRepository.findOrderDetailByOrderId(user.getCurrentOrder().getId())){
            if (orderDetail.getPackageEntity().getId() == pkg.getId()){
                orderDetail.setQuantity(quantity);
                orderDetailRepository.save(orderDetail);
                return;
            }
        }
        OrderDetail newOrderDetail = new OrderDetail();
        newOrderDetail.setQuantity(quantity); // check quantity or not?
        newOrderDetail.setPackageEntity(pkg);
        newOrderDetail.setComboPrice(pkg.getPrice());
        newOrderDetail.setOrder(user.getCurrentOrder());
        orderDetailRepository.save(newOrderDetail);
    }

    @Override
    public void updateOrderDetail(int orderDetailId, int quantity) {
        OrderDetail existedOrderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow(() -> new OrderDetailNotFoundException("Order detail could not be found"));
        if (quantity == 0) {
            orderDetailRepository.delete(existedOrderDetail);
        } else {
            existedOrderDetail.setQuantity(quantity);
            orderDetailRepository.save(existedOrderDetail);
        }
    }

    @Override
    @Transactional
    public List<DeliveryOrderDto> getOrderHistory(UUID userId) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User could not be found"));
        return currentUser.getOrders().stream().map(this::mapToDeliveryOrderDto).toList();
    }

    @Override
    @Transactional
    public OrderDetailAfterLoginRequest getOrderDetailAfterLogin(UUID userId) {
        OrderDetailAfterLoginRequest orderDetailAfterLoginRequest = new OrderDetailAfterLoginRequest();
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User could not be found"));

        if (currentUser.getCurrentOrder() != null){
            int currentOrderId = currentUser.getCurrentOrder().getId();

            List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailByOrderId(currentOrderId);
            if (orderDetailList.isEmpty()){
                return orderDetailAfterLoginRequest;
            }
            List<OrderDetailDto> orderDetailDtoList = orderDetailList.stream().map(this::mapToOrderDetailDto).toList();

            orderDetailAfterLoginRequest.setOrderId(currentOrderId);
            orderDetailAfterLoginRequest.setOrderDetailList(orderDetailDtoList);
        }
        return orderDetailAfterLoginRequest;
    }

    public static String generateOrderCode(int orderNumber) {
        String prefix = "OD-";
        String formattedNumber = String.format("%05d", orderNumber);
        return prefix + formattedNumber;
    }

    @Override
    @Transactional
    public boolean checkOutOrder(DeliveryOrderDtoForCheckOut deliveryOrderDtoForCheckOut) {
        DeliveryOrder order = deliveryOrderRepository.findById(deliveryOrderDtoForCheckOut.getOrderId().longValue()).orElseThrow(() -> new RuntimeException("Order could not be found"));
        if (order.getStatus().equalsIgnoreCase("Ordering")){
            order.setOrderDate(new Date());
            order.setOrderValue(deliveryOrderDtoForCheckOut.getOrderValue() != null ? deliveryOrderDtoForCheckOut.getOrderValue().floatValue() : 0);
            order.setShippedAddress(deliveryOrderDtoForCheckOut.getShippedAddress());
            order.setLatitude(deliveryOrderDtoForCheckOut.getLatitude());
            order.setLongitude(deliveryOrderDtoForCheckOut.getLongitude());
            order.setShippingFee(deliveryOrderDtoForCheckOut.getShippingFee() != null ? deliveryOrderDtoForCheckOut.getShippingFee().floatValue() : 0);
            order.setStatus("Processing");
            order.setPaymentMethod(paymentMethodRepository.findByMethod(deliveryOrderDtoForCheckOut.getPaymentMethod()));
            order.getAccount().setCurrentOrder(null);
            order.setAccountWithCurrentOrder(null);
            DeliveryOrder savedOrder = deliveryOrderRepository.save(order);
            savedOrder.setCode(generateOrderCode(savedOrder.getId()));
            deliveryOrderRepository.save(savedOrder);
            return true;
        } else {
            return false;//
        }
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
    public ResponseEntity<Object> cancelOrderForManager(Long orderId) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("User not found"));

        if (deliveryOrder.getStatus() != null) {
            if (deliveryOrder.getStatus().equalsIgnoreCase("Delivered")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is being delivered and cannot be cancelled");
            }else if(deliveryOrder.getStatus().equalsIgnoreCase("Cancelled")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order cancelled already");
            }else {
                deliveryOrder.setStatus("Cancelled");
                deliveryOrderRepository.save(deliveryOrder);
                return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Cancel order successfully", "");
            }
        }
        return null;
    }


    @Override
    public ResponseEntity<Object> getRevenueByPeriod(int pageNo, int pageSize, String sortBy, String sortDir, String period, LocalDate startDate) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        List<DeliveryOrder> deliveryOrders = deliveryOrderRepository.getDeliveryOrder(startDate);
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

        Page<RevenueElementResponse> deliveryOrderPage = convertListToPage(content, pageable);
        List<RevenueElementResponse> contentList = deliveryOrderPage.getContent();

        RevenueResponse revenueResponse = new RevenueResponse();
        revenueResponse.setContent(contentList);
        revenueResponse.setPageNo(deliveryOrderPage.getNumber());
        revenueResponse.setPageSize(deliveryOrderPage.getSize());
        revenueResponse.setTotalElements(deliveryOrderPage.getTotalElements());
        revenueResponse.setTotalPages(deliveryOrderPage.getTotalPages());
        revenueResponse.setLast(deliveryOrderPage.isLast());

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved revenue", revenueResponse);
    }

    @Override
    public ResponseEntity<Object> getDeliveryOrderDetailByAdmin(int pageNo, int pageSize, String sortBy, String sortDir, String period, LocalDate startDate) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<DeliveryOrder> deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByDaily(pageable, startDate);

        if (period.equalsIgnoreCase("yearly")) {
            deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByYear(pageable, startDate.getYear());
        } else if (period.equalsIgnoreCase("monthly")) {
            deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByMonth(pageable, startDate.getYear(), startDate.getMonthValue());
        }

        RevenueDetailResponse revenueResponse = extractDeliveryOrderDetail(deliveryOrderPage, pageable);
        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved revenue by daily", revenueResponse);
    }

    @Override
    public ResponseEntity<Object> getDeliveryOrderDetailByShipperId(int pageNo, int pageSize, String sortBy, String sortDir, int shipperId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<DeliveryOrder> deliveryOrderPage = deliveryOrderRepository.getByShipperId(shipperId, pageable);

        RevenueDetailResponse revenueResponse = extractDeliveryOrderDetail(deliveryOrderPage, pageable);

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved Delivery Order Detail by shipper", revenueResponse);
    }

    @Override
    public ResponseEntity<Object> getDeliveryOrderByStatus(int pageNo, int pageSize, String sortBy, String sortDir, String status) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<DeliveryOrder> deliveryOrderPage = deliveryOrderRepository.getDeliveryOrderByStatus(status, pageable);
        List<DeliveryOrder> deliveryOrders = deliveryOrderPage.getContent();

        List<DeliveryOrderElement> content = deliveryOrders.stream()
                .map(deliveryOrder -> modelMapper.map(deliveryOrder, DeliveryOrderElement.class))
                .collect(Collectors.toList());

        for (DeliveryOrderElement d : content) {
            List<String> packageName = new ArrayList<>();
            float deliveryOrderPrice = 0;
            List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderId(d.getId());

            //tính tiền
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
            d.setPhone(users.getPhone());
            d.setAddress(users.getAddress());

        }


        DeliveryOrderForManager deliveryOrderForManager = new DeliveryOrderForManager();
        deliveryOrderForManager.setContent(content);
        deliveryOrderForManager.setTotalPages(deliveryOrderPage.getTotalPages());
        deliveryOrderForManager.setTotalElements(deliveryOrderPage.getTotalElements());
        deliveryOrderForManager.setPageNo(deliveryOrderPage.getNumber());
        deliveryOrderForManager.setPageSize(deliveryOrderPage.getSize());
        deliveryOrderForManager.setLast(deliveryOrderPage.isLast());

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK, "Successfully retrieved User's ordering for Manager ", deliveryOrderForManager);
    }

    private RevenueDetailResponse extractDeliveryOrderDetail(Page<DeliveryOrder> deliveryOrderPage, Pageable pageable) {

        List<DeliveryOrder> deliveryOrders = deliveryOrderPage.getContent();
        List<RevenueDetailElementResponse> content = deliveryOrders.stream()
                .map(deliveryOrder -> modelMapper.map(deliveryOrder, RevenueDetailElementResponse.class))
                .collect(Collectors.toList());

        //Lặp qua từng delivery order để lấy ra các package name
        for (RevenueDetailElementResponse d : content) {
            //Lấy ra các package name
            List<String> packageName = new ArrayList<>();
            float deliveryOrderPrice = 0;
            List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderId(d.getId());
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

        }
        RevenueDetailResponse revenueResponse = new RevenueDetailResponse();
        revenueResponse.setContent(content);
        revenueResponse.setPageNo(pageable.getPageNumber());
        revenueResponse.setPageSize(pageable.getPageSize());
        revenueResponse.setTotalElements(deliveryOrderPage.getTotalElements());
        revenueResponse.setTotalPages(deliveryOrderPage.getTotalPages());
        revenueResponse.setLast(deliveryOrderPage.isLast());


        return revenueResponse;
    }

    private Map<Object, List<DeliveryOrder>> groupByPeriod(List<DeliveryOrder> deliveryOrders, String period) {

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

    private String getDateTimeFormatter(Object mapKey, String period) {
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

    private Page<RevenueElementResponse> convertListToPage(List<RevenueElementResponse> content, Pageable pageable) {
        List<RevenueElementResponse> sortedContent = content.stream()
                .sorted((e1, e2) -> {
                    for (Sort.Order order : pageable.getSort()) {
                        Comparator<RevenueElementResponse> comparator;

                        switch (order.getProperty()) {
                            case "totalRevenue":
                                comparator = Comparator.comparing(RevenueElementResponse::getTotalRevenue);
                                break;
                            case "completedOrder":
                                comparator = Comparator.comparing(RevenueElementResponse::getCompletedOrder);
                                break;
                            case "cancelledOrder":
                                comparator = Comparator.comparing(RevenueElementResponse::getCancelledOrder);
                                break;
                            default:
                                comparator = Comparator.comparing(RevenueElementResponse::getOrderDate);
                        }

                        if (order.getDirection().isDescending()) {
                            comparator = comparator.reversed();
                        }
                        int comparison = comparator.compare(e1, e2);
                        if (comparison != 0) {
                            return comparison;
                        }
                    }
                    return 0; // Nếu không có thuộc tính sắp xếp hoặc sắp xếp giống nhau
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedContent.size());
        List<RevenueElementResponse> subList = sortedContent.subList(start, end);
        return new PageImpl<>(subList, pageable, sortedContent.size());
    }

    private OrderDetailDto mapToOrderDetailDto(OrderDetail orderDetail){
        return OrderDetailDto.builder()
                .orderDetailId(orderDetail.getId())
                .packageId(orderDetail.getPackageEntity().getId())
                .packageName(orderDetail.getPackageEntity().getName())
                .packageType(orderDetail.getPackageEntity().getPackageType())
                .packageSize(orderDetail.getPackageEntity().getPackageSize())
                .packagePrice(orderDetail.getComboPrice() != null ? orderDetail.getComboPrice().longValue() : 0)
                .thumbnailUrl(orderDetail.getPackageEntity().getThumbnailUrl())
                .packageQuantity(orderDetail.getQuantity())
                .build();
    }

    private OrderDetailDtoForOrderHistory mapToOrderDetailDtoForOrderHistory(OrderDetail orderDetail){
        return OrderDetailDtoForOrderHistory.builder()
                .orderDetailId(orderDetail.getId())
                .packageId(orderDetail.getPackageEntity().getId())
                .packageName(orderDetail.getPackageEntity().getName())
                .packagePrice(orderDetail.getComboPrice() != null ? orderDetail.getComboPrice().longValue() : 0)
                .thumbnailUrl(orderDetail.getPackageEntity().getThumbnailUrl())
                .packageQuantity(orderDetail.getQuantity())
                .build();
    }

    private DeliveryOrderDto mapToDeliveryOrderDto(DeliveryOrder deliveryOrder){
        return DeliveryOrderDto.builder()
                .orderId(deliveryOrder.getId())
                .orderDate(deliveryOrder.getOrderDate())
                .orderValue(deliveryOrder.getOrderValue() != null ? deliveryOrder.getOrderValue().longValue() : 0)
                .shippedDate(deliveryOrder.getShippedDate())
                .shippedAddress(deliveryOrder.getShippedAddress())
                .shippingFee(deliveryOrder.getShippingFee() != null ? deliveryOrder.getShippingFee().longValue() : 0)
                .status(deliveryOrder.getStatus())
                .paymentMethod(deliveryOrder.getPaymentMethod() != null ? deliveryOrder.getPaymentMethod().getMethod() : null)
                .orderDetails(deliveryOrder.getOrderDetails().stream().map(this::mapToOrderDetailDtoForOrderHistory).toList())
                .build();
    }
}
