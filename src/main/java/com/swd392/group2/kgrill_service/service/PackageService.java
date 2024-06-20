package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_model.model.Package;
import org.springframework.data.domain.Page;

public interface PackageService{

    void AddPackage(Package pkg);

    void UpdatePackage(Package pkg);

    void DeletePackageById(int id);

    Page<Package> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir);

}
