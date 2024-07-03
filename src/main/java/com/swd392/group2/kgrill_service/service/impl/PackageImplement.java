package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.model.PackageDish;
import com.swd392.group2.kgrill_model.repository.PackageRepository;
import com.swd392.group2.kgrill_service.dto.request.PackageRequest;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForAdminAndManager;
import com.swd392.group2.kgrill_service.dto.mobiledto.PackageDishDtoOnMobile;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForMobileAsList;
import com.swd392.group2.kgrill_service.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageImplement implements PackageService {

    private final PackageRepository packageRepository;
//    private final PackageDishRepository packageDishRepository;

    public static String generatePackageCode(int packageNumber) {
        String prefix = "FP-";
        String formattedNumber = String.format("%03d", packageNumber);
        return prefix + formattedNumber;
    }

    @Override
    public void addPackage(PackageRequest pkgRequest) {
        Package pkg = mapToPackage(pkgRequest);
        Package savedPackage = packageRepository.save(pkg);
        savedPackage.setCode(generatePackageCode(savedPackage.getId()));
        packageRepository.save(savedPackage);
    }

    @Override
    public void updatePackage(PackageRequest pkgRequest) {
        Package pkg = mapToPackage(pkgRequest);
        packageRepository.save(pkg);
    }

    @Override
    public void deletePackageById(int id) {
        packageRepository.deleteById(id);
    }

    @Override
    public Page<PackageResponseForAdminAndManager> searchPackageByFilter(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return null;
    }

    @Override
    public Page<PackageResponseForAdminAndManager> getAllPackagePages(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Package> packages = packageRepository.findAll(pageable);
        return packages.map(this::mapToPackageResponseForAdmin);
    }

    @Override
    public List<PackageResponseForMobileAsList> getAllPackageOnMobile() {
        return List.of();
    }

    private PackageResponseForAdminAndManager mapToPackageResponseForAdmin(Package pkg) {
        return PackageResponseForAdminAndManager.builder()
            .id(pkg.getId())
            .name(pkg.getName())
            .code(pkg.getCode())
            .price(pkg.getPrice())
            .active(pkg.isActive())
            .thumbnail(pkg.getThumbnail())
            .build();
    }

    private Package mapToPackage(PackageRequest pkgRequest) {
        return Package.builder()
            .id(pkgRequest.getId())
            .name(pkgRequest.getName())
            .description(pkgRequest.getDescription())
            .price(pkgRequest.getPrice())
            .active(pkgRequest.isActive())
            .thumbnail(pkgRequest.getThumbnail())
            .build();
    }

    public PackageDishDtoOnMobile mapToPackageDishDto(PackageDish packageDish) {
        PackageDishDtoOnMobile dto = new PackageDishDtoOnMobile();
        dto.setDishName(packageDish.getDish().getName());
        dto.setDishPrice(packageDish.getDishPrice());
        dto.setQuantity(packageDish.getQuantity());
        return dto;
    }
}
