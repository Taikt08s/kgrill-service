package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.RegistrationRequest;
import com.swd392.group2.kgrill_service.dto.ShipperDto;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForAdminAndManager;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ShipperService {

    ResponseEntity<Object> getAllShippersByAdmin(int pageNo, int pageSize, String sortBy, String sortDir);

    Page<ShipperDto> getAvailableShipperList(int pageNumber, int pageSize, String sortField, String sortDir);

}
