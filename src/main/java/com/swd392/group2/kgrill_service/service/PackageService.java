package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.request.PackageRequest;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForAdminAndManager;
import org.springframework.data.domain.Page;

public interface PackageService{

    void addPackage(PackageRequest pkgRequest);

    void updatePackage(PackageRequest pkgRequest);

    void deletePackageById(int id);

    Page<PackageResponseForAdminAndManager> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir);

    Page<PackageResponseForAdminAndManager> getAllPackagePages(int pageNumber, int pageSize, String sortField, String sortDir);
}
