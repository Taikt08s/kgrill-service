package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.repository.PackageRepository;
import com.swd392.group2.kgrill_service.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackageImplement implements PackageService {

    private final PackageRepository packageRepository;

    public static String generatePackageCode(int packageNumber) {
        String prefix = "FP-";
        String formattedNumber = String.format("%03d", packageNumber);
        return prefix + formattedNumber;
    }

    @Override
    public void AddPackage(Package pkg) {
        Package savedPackage = packageRepository.save(pkg);
        savedPackage.setCode(generatePackageCode(savedPackage.getId()));
        packageRepository.save(savedPackage);
    }

    @Override
    public void UpdatePackage(Package pkg) {
        packageRepository.save(pkg);
    }

    @Override
    public void DeletePackageById(int id) {
        packageRepository.deleteById(id);
    }

    @Override
    public Page<Package> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir) {
        return null;
    }
}
