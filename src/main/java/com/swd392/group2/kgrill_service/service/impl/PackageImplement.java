package com.swd392.group2.kgrill_service.service.impl;

import com.swd392.group2.kgrill_model.model.Dish;
import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.model.PackageDish;
import com.swd392.group2.kgrill_model.repository.DishRepository;
import com.swd392.group2.kgrill_model.repository.PackageDishRepository;
import com.swd392.group2.kgrill_model.repository.PackageRepository;
import com.swd392.group2.kgrill_service.dto.PackageDishDto;
import com.swd392.group2.kgrill_service.dto.request.PackageRequest;
import com.swd392.group2.kgrill_service.dto.response.PackageDetailResponseForMobile;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForAdminAndManager;
import com.swd392.group2.kgrill_service.dto.mobiledto.PackageDishDtoOnMobile;
import com.swd392.group2.kgrill_service.dto.response.PackageResponseForMobileAsList;
import com.swd392.group2.kgrill_service.exception.DishNotFoundException;
import com.swd392.group2.kgrill_service.exception.PackageNotFoundException;
import com.swd392.group2.kgrill_service.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageImplement implements PackageService {

    private final PackageRepository packageRepository;
    private final PackageDishRepository packageDishRepository;
    private final DishRepository dishRepository;

    public static String generatePackageCode(int packageNumber) {
        String prefix = "FP-";
        String formattedNumber = String.format("%03d", packageNumber);
        return prefix + formattedNumber;
    }

    @Override
    public void addPackage(PackageRequest pkgRequest, String imgUrl) {
        List<PackageDishDto> pkgDishDtoList = pkgRequest.getPackageDishList();
        pkgRequest.setThumbnailUrl(imgUrl);
        Package pkg = mapToPackage(pkgRequest);
        List<PackageDish> pkgDishList = new ArrayList<>();
        for (PackageDishDto pkgDishDto : pkgDishDtoList) {
            Dish dish = dishRepository.findById(pkgDishDto.getId()).orElseThrow(() -> new DishNotFoundException("Dish could not be found"));
            PackageDish pkgDish = new PackageDish();
            pkgDish.setDishPrice(dish.getPrice());
            pkgDish.setPackageEntity(pkg);
            pkgDish.setQuantity(pkgDishDto.getQuantity());
            pkgDish.setDish(dish);
            pkgDishList.add(pkgDish);
        }
        pkg.setPackageDishes(pkgDishList);
        Package savedPackage = packageRepository.save(pkg);
        savedPackage.setCode(generatePackageCode(savedPackage.getId()));
        packageRepository.save(savedPackage);
    }

    @Override
    @Transactional
    public void updatePackage(PackageRequest pkgRequest) {
        List<PackageDishDto> pkgDishDtoList = pkgRequest.getPackageDishList();
        Package updatedPackage = mapToPackage(pkgRequest);
        Package existedPackage = packageRepository.findById(pkgRequest.getId()).orElseThrow(() -> new PackageNotFoundException("Package could not be found"));
        updatedPackage.setCode(existedPackage.getCode());
        updatedPackage.setOrderDetails(existedPackage.getOrderDetails());

        List<Integer> existedPackageDishIdList = existedPackage.getPackageDishes()
                .stream()
                .map(PackageDish -> PackageDish.getDish().getId())
                .toList();

        List<Integer> newPackageDishIdList = pkgDishDtoList
                .stream().map(PackageDishDto::getId)
                .toList();

        List<Integer> removePackageDishIdList = existedPackageDishIdList.stream()
                .filter(existedPackageDishId -> !newPackageDishIdList.contains(existedPackageDishId))
                .toList();

        for (PackageDishDto pkgDishDto : pkgDishDtoList) {
            int existedDishIdPosition = existedPackageDishIdList.indexOf(pkgDishDto.getId());
            if (existedDishIdPosition == -1) {
                Dish dish = dishRepository.findById(pkgDishDto.getId()).orElseThrow(() -> new DishNotFoundException("Dish could not be found"));
                PackageDish pkgDish = new PackageDish();
                pkgDish.setDishPrice(dish.getPrice());
                pkgDish.setPackageEntity(updatedPackage);
                pkgDish.setQuantity(pkgDishDto.getQuantity());
                pkgDish.setDish(dish);
                updatedPackage.getPackageDishes().add(pkgDish);
            }else {
                int existedDishId = existedPackageDishIdList.get(existedDishIdPosition);
                PackageDish pkgDish = packageDishRepository.findByPackageEntity_IdAndDish_Id(updatedPackage.getId(), existedDishId);
                pkgDish.setQuantity(pkgDishDto.getQuantity());
                updatedPackage.getPackageDishes().add(pkgDish);
            }
        }

        for (Integer removePackageDishId : removePackageDishIdList) {
            PackageDish pkgDish = packageDishRepository.findByPackageEntity_IdAndDish_Id(updatedPackage.getId(), removePackageDishId);
            packageDishRepository.delete(pkgDish);
        }

        packageRepository.save(updatedPackage);
    }

    @Override
    public void deletePackageById(int id) {
        packageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PackageRequest getAPackageDetail(int pkgId) {
        Package pkg = packageRepository.findById(pkgId).orElseThrow(() -> new PackageNotFoundException("Package could not be found"));
        List<PackageDishDto> dishDtoList = pkg.getPackageDishes().stream().map(this::mapToPackageDishDto).toList();
        return mapToPackageRequest(pkg, dishDtoList);
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
        return packages.map(this::mapToPackageResponseForAdminAndManager);
    }

    @Override
    public List<PackageResponseForMobileAsList> getAllPackageOnMobile() {
        return packageRepository.findAll().stream().map(this::mapToPackageResponseForMobileAsList).toList();
    }

    @Override
    public PackageDetailResponseForMobile getPackageDetailOnMobile(int id) {
        Package pkg = packageRepository.findById(id).orElseThrow(() -> new PackageNotFoundException("Package could not be found"));
        List<PackageDish> dishList = packageDishRepository.findAllByPackageEntity_Id(id);
        List<PackageDishDtoOnMobile> dishDtoList = dishList.stream().map(this::mapToPackageDishDtoOnMobile).toList();
        return mapToPackageDetailResponseForMobile(pkg, dishDtoList);
    }

    private PackageResponseForAdminAndManager mapToPackageResponseForAdminAndManager(Package pkg) {
        return PackageResponseForAdminAndManager.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .code(pkg.getCode())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .active(pkg.isActive())
                .thumbnailUrl(pkg.getThumbnailUrl())
                .build();
    }

    private PackageResponseForMobileAsList mapToPackageResponseForMobileAsList(Package pkg) {
        return PackageResponseForMobileAsList.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .price(pkg.getPrice())
                .packageType(pkg.getPackageType())
                .thumbnailUrl(pkg.getThumbnailUrl())
                .build();
    }

    public PackageDishDtoOnMobile mapToPackageDishDtoOnMobile(PackageDish packageDish) {
        PackageDishDtoOnMobile packageDishDtoOnMobile = new PackageDishDtoOnMobile();
        packageDishDtoOnMobile.setDishName(packageDish.getDish().getName());
        packageDishDtoOnMobile.setDishPrice(packageDish.getDishPrice());
        packageDishDtoOnMobile.setQuantity(packageDish.getQuantity());
        return packageDishDtoOnMobile;
    }

    public PackageDishDto mapToPackageDishDto(PackageDish packageDish) {
        PackageDishDto packageDishDto = new PackageDishDto();
        packageDishDto.setId(packageDish.getDish().getId());
        packageDishDto.setName(packageDish.getDish().getName());
        packageDishDto.setQuantity(packageDish.getQuantity());
        return packageDishDto;
    }

    public PackageDetailResponseForMobile mapToPackageDetailResponseForMobile(Package pkg, List<PackageDishDtoOnMobile> dishDtoList) {
        return PackageDetailResponseForMobile.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .price(pkg.getPrice())
                .packageSize(pkg.getPackageSize())
                .packageDishes(dishDtoList)
                .packageType(pkg.getPackageType())
                .description(pkg.getDescription())
                .thumbnailUrl(pkg.getThumbnailUrl())
                .build();
    }

    private Package mapToPackage(PackageRequest pkgRequest) {
        return Package.builder()
                .id(pkgRequest.getId())
                .name(pkgRequest.getName())
                .description(pkgRequest.getDescription())
                .code(pkgRequest.getCode())
                .price(pkgRequest.getPrice())
                .packageDishes(new ArrayList<>())
                .packageType(pkgRequest.getPackageType())
                .packageSize(pkgRequest.getPackageSize())
                .active(pkgRequest.isActive())
                .thumbnailUrl(pkgRequest.getThumbnailUrl())
                .build();
    }

    private PackageRequest mapToPackageRequest(Package pkg, List<PackageDishDto> dishDtoList) {
        return PackageRequest.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .description(pkg.getDescription())
                .code(pkg.getCode())
                .price(pkg.getPrice())
                .packageType(pkg.getPackageType())
                .packageSize(pkg.getPackageSize())
                .active(pkg.isActive())
                .packageDishList(dishDtoList)
                .thumbnailUrl(pkg.getThumbnailUrl())
                .build();
    }
}
