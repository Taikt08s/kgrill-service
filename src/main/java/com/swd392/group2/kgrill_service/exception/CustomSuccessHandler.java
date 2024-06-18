package com.swd392.group2.kgrill_service.exception;

import com.swd392.group2.kgrill_service.util.DateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomSuccessHandler {
    public static ResponseEntity<Object> responseBuilder(HttpStatus httpStatus, String message, Object responseObject) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("http-status", httpStatus.value());
        response.put("timestamp", DateUtil.formatTimestamp(new Date()));
        response.put("message", message);
        response.put("data", responseObject);

        return new ResponseEntity<>(response, httpStatus);
    }
}

