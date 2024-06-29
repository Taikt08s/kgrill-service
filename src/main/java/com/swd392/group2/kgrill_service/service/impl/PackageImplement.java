package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.repository.PackageRepository;
import com.swd392.group2.kgrill_service.dto.PackageRequest;
import com.swd392.group2.kgrill_service.dto.PackageResponseForAdmin;
import com.swd392.group2.kgrill_service.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public void AddPackage(PackageRequest pkgRequest) {
        Package pkg = maptoPackage(pkgRequest);
        Package savedPackage = packageRepository.save(pkg);
        savedPackage.setCode(generatePackageCode(savedPackage.getId()));
        packageRepository.save(savedPackage);
    }

    @Override
    public void UpdatePackage(PackageRequest pkgRequest) {
        Package pkg = maptoPackage(pkgRequest);
        packageRepository.save(pkg);
    }

    @Override
    public void DeletePackageById(int id) {
        packageRepository.deleteById(id);
    }

    @Override
    public Page<PackageResponseForAdmin> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return null;
    }

    @Override
    public Page<PackageResponseForAdmin> GetAllPackagePages(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Package> packages = packageRepository.findAll(pageable);
        return packages.map(this::maptoPackageResponseForAdmin);
    }

    private PackageResponseForAdmin maptoPackageResponseForAdmin(Package pkg) {
        return PackageResponseForAdmin.builder()
            .id(pkg.getId())
            .name(pkg.getName())
            .code(pkg.getCode())
            .price(pkg.getPrice())
            .active(pkg.isActive())
            .thumbnail(pkg.getThumbnail())
            .build();
    }

    private Package maptoPackage(PackageRequest pkgRequest) {
        return Package.builder()
            .id(pkgRequest.getId())
            .name(pkgRequest.getName())
            .description(pkgRequest.getDescription())
            .price(pkgRequest.getPrice())
            .active(pkgRequest.isActive())
            .thumbnail(pkgRequest.getThumbnail())
            .build();
    }
}
