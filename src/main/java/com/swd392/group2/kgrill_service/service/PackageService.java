package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_service.dto.PackageRequest;
import com.swd392.group2.kgrill_service.dto.PackageResponseForAdmin;
import org.springframework.data.domain.Page;

public interface PackageService{

    void AddPackage(PackageRequest pkgRequest);

    void UpdatePackage(PackageRequest pkgRequest);

    void DeletePackageById(int id);

    Page<PackageResponseForAdmin> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir);

    Page<PackageResponseForAdmin> GetAllPackagePages(int pageNumber, int pageSize, String sortField, String sortDir);
}
