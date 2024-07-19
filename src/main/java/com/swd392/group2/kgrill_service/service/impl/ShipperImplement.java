package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.DeliveryOrder;
import com.swd392.group2.kgrill_model.model.OrderDetail;
import com.swd392.group2.kgrill_model.model.Shipper;
import com.swd392.group2.kgrill_model.model.User;
import com.swd392.group2.kgrill_model.repository.DeliveryOrderRepository;
import com.swd392.group2.kgrill_model.repository.OrderDetailRepository;
import com.swd392.group2.kgrill_model.repository.ShipperRepository;
import com.swd392.group2.kgrill_model.repository.UserRepository;
import com.swd392.group2.kgrill_service.dto.RevenueDetailElementResponse;
import com.swd392.group2.kgrill_service.dto.RevenueDetailResponse;
import com.swd392.group2.kgrill_service.dto.ShipperDto;
import com.swd392.group2.kgrill_service.dto.response.ShipperInfoElement;
import com.swd392.group2.kgrill_service.dto.response.ShipperInfoResponse;
import com.swd392.group2.kgrill_service.exception.CustomSuccessHandler;
import com.swd392.group2.kgrill_service.exception.ResourceNotFoundException;
import com.swd392.group2.kgrill_service.service.ShipperService;

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
public class ShipperImplement implements ShipperService {

    private final DeliveryOrderRepository deliveryOrderRepository;
    private final ShipperRepository shipperRepository;
    private final UserRepository userRepository;


    @Override
    public ResponseEntity<Object> getAllShippersByAdmin(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Shipper> shippers = shipperRepository.findAll(pageable);
        List<Shipper> shipperList = shippers.getContent();

        List<ShipperInfoElement> content = new ArrayList<>();
        for (Shipper shipper : shipperList) {

            ShipperInfoElement shipperInfoElement = new ShipperInfoElement();

            User user = userRepository.findById(UUID.fromString(shipper.getUuid()))
                    .orElseThrow( () -> new UsernameNotFoundException("User not found"));

            shipperInfoElement.setId(shipper.getId());
            shipperInfoElement.setUuid(shipper.getUuid());
            shipperInfoElement.setName(user.getFirstName() + " " + user.getLastName());
            shipperInfoElement.setTotalOrder(deliveryOrderRepository.countByShipperId(shipper.getId()));
            shipperInfoElement.setCompletedOrder(deliveryOrderRepository.countByShipperIdAndStatus(shipper.getId(), "Delivered"));
            shipperInfoElement.setCancelledOrder(deliveryOrderRepository.countByShipperIdAndStatus(shipper.getId(), "Cancelled"));
            content.add(shipperInfoElement);
        }

        ShipperInfoResponse shipperInfoResponse = new ShipperInfoResponse();
        shipperInfoResponse.setContent(content);
        shipperInfoResponse.setTotalElements(shippers.getTotalElements());
        shipperInfoResponse.setTotalPages(shippers.getTotalPages());
        shipperInfoResponse.setPageNo(shippers.getNumber());
        shipperInfoResponse.setPageSize(shippers.getSize());
        shipperInfoResponse.setLast(shippers.isLast());

        return CustomSuccessHandler.responseBuilder(HttpStatus.OK,"Successfully retrieved all shippers", shipperInfoResponse);
    }

    @Override
    public Page<ShipperDto> getAvailableShipperList(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Shipper> shipperList = shipperRepository.findAllByStatus("Available", pageable);
        return shipperList.map(this::mapToShipperDto);
    }

    @Override
    public boolean assignShipperToOrder(long shipperId, long orderId) {
        Shipper shipper = shipperRepository.findById(shipperId).orElseThrow(() -> new RuntimeException("Shipper could not be found"));
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order could not be found"));
        if (shipper.getStatus().equalsIgnoreCase("Available") && deliveryOrder.getStatus().equalsIgnoreCase("Preparing")){
            deliveryOrder.setShipper(shipper);
            deliveryOrderRepository.save(deliveryOrder);
            return true;
        }
        return false;
    }

    private ShipperDto mapToShipperDto(Shipper shipper){
        User user = userRepository.findById(UUID.fromString(shipper.getUuid()))
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
        return ShipperDto.builder()
                .shipperId(shipper.getId())
                .shipperName(user.fullName())
                .shipperPhone(user.getPhone())
                .shipperStatus(shipper.getStatus())
                .build();
    }
}