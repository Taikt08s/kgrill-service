package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_service.dto.request.PackageRequest;
import com.swd392.group2.kgrill_service.dto.response.PackageDetailResponseForMobile;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForAdminAndManager;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForMobileAsList;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface PackageService{

    int addPackage(PackageRequest pkgRequest);

    PackageRequest preAddPackage();

    void updatePackage(PackageRequest pkgRequest);

    void deletePackageById(int id);

    void uploadPackageThumbnail(int packageId, String thumbnailUrl);

    PackageRequest getAPackageDetail(int pkgId);

    Page<PackageResponseForAdminAndManager> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir);

    Page<PackageResponseForAdminAndManager> getAllPackagePages(int pageNumber, int pageSize, String sortField, String sortDir);

    List<PackageResponseForMobileAsList> getAllPackageOnMobile();

    PackageDetailResponseForMobile getPackageDetailOnMobile(int id);
}
