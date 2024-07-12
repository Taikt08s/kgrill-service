package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.RegistrationRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ShipperService {

    ResponseEntity<Object> getAllShippersByAdmin(int pageNo, int pageSize, String sortBy, String sortDir);

}
