package com.swd392.group2.kgrill_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryUploadService {
    String uploadFile(MultipartFile multipartFile) throws IOException;
}
