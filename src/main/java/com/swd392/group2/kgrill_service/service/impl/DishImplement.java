package com.swd392.group2.kgrill_service.service.impl;


import com.swd392.group2.kgrill_model.model.Dish;
import com.swd392.group2.kgrill_model.model.DishIngredient;
import com.swd392.group2.kgrill_model.model.Ingredient;
import com.swd392.group2.kgrill_model.model.Package;
import com.swd392.group2.kgrill_model.repository.DishIngredientRepository;
import com.swd392.group2.kgrill_model.repository.DishRepository;
import com.swd392.group2.kgrill_model.repository.IngredientRepository;
import com.swd392.group2.kgrill_service.dto.DishDTO;
import com.swd392.group2.kgrill_service.dto.DishIngredientDTO;
import com.swd392.group2.kgrill_service.dto.PackageDishDto;
import com.swd392.group2.kgrill_service.dto.request.DishRequest;
import com.swd392.group2.kgrill_service.dto.request.PackageRequest;
import com.swd392.group2.kgrill_service.exception.DishNotFoundException;
import com.swd392.group2.kgrill_service.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class DishImplement implements DishService {
    private DishRepository dishRepository;
    private IngredientRepository ingredientRepository;
    private DishIngredientRepository dishIngredientRepository;

    @Autowired
    public DishImplement(DishRepository dishRepository, IngredientRepository ingredientRepository, DishIngredientRepository dishIngredientRepository)
    {
        this.ingredientRepository = ingredientRepository;
        this.dishRepository=dishRepository;
        this.dishIngredientRepository= dishIngredientRepository;

    }

//    @Override
//    public List<Dish> findByNameAndPrice(String keyword) {
//
//    }

    @Override
    public DishRequest createDish(DishRequest dishRequest) {
        List<DishIngredientDTO> dishIngredientDtoList = dishRequest.getDishIngredientList();
        Dish dish = mapToEntity(dishRequest);
        List<DishIngredient> dishIngredientList = new ArrayList<>();
        for (DishIngredientDTO dishIngredientDTO : dishIngredientDtoList){
            Ingredient in = ingredientRepository.findById(dishIngredientDTO.getId()).orElseThrow(() -> new DishNotFoundException("Ingredient could not be found"));
            DishIngredient dishIngredient = new DishIngredient();
            dishIngredient.setId(in.getId());
            dishIngredient.setDish(dish);
            dishIngredient.setIngredient(in);
            dishIngredientList.add(dishIngredient);

        }
        dish.setDishIngredients(dishIngredientList);
        Dish newDish = dishRepository.save(dish);
//        DishDTO dishResponse = new DishDTO();
//        dishResponse.setId(newDish.getId());
//        dishResponse.setName(newDish.getName());
//        dishResponse.setPrice(newDish.getPrice());

        return mapToDishRequest(newDish, dishIngredientDtoList);
    }

//    @Override
//    public List<DishDTO> getAllDish() {
//        List<Dish> dishes = dishRepository.findAll();
//        return dishes.stream().map(d -> mapToDto(d)).collect(Collectors.toList());
//    }

    @Override
    public DishRequest getDishByID(int id) {
        Dish dish = dishRepository.findById(id).orElseThrow(()->new DishNotFoundException("Dish could not be found"));
        List<DishIngredientDTO> dishIngredientDTOList = dish.getDishIngredients().stream().map(this::maptoDishIngredientDTO).toList();
//        List<PackageDishDto> dishDtoList = pkg.getPackageDishes().stream().map(this::mapToPackageDishDto).toList();
//        return mapToPackageRequest(pkg, dishDtoList);

        return mapToDishRequest(dish, dishIngredientDTOList);
    }

    @Override
    public DishRequest updateDish(DishRequest dishRequest, int id) {
        List<DishIngredientDTO> dishIngredientDtoList = dishRequest.getDishIngredientList();
        Dish dish = mapToEntity(dishRequest);
        List<DishIngredient> dishIngredientList = new ArrayList<>();
        for (DishIngredientDTO dishIngredientDTO : dishIngredientDtoList){
            Ingredient in = ingredientRepository.findById(dishIngredientDTO.getId()).orElseThrow(() -> new DishNotFoundException("Ingredient could not be found"));
            DishIngredient dishIngredient = new DishIngredient();
            dishIngredient.setId(in.getId());
            dishIngredient.setDish(dish);
            dishIngredient.setIngredient(in);
            dishIngredientList.add(dishIngredient);
        }
        dish.setDishIngredients(dishIngredientList);
        Dish updatedDish= dishRepository.save(dish);
        return mapToDishRequest(updatedDish, dishIngredientDtoList);

    }

    @Override
    public void deleteDish(int id) {
        Dish dish = dishRepository.findById(id).orElseThrow(()->new DishNotFoundException("Dish could not be found"));
        dishRepository.delete(dish);
    }

    @Override
    public Page<Dish> searchDishByFilter(int pageNumber, int pageSize, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Dish> dishes= dishRepository.findByNameAndPrice(keyword, pageable);

        return dishes;

    }

    @Override
    public Page<DishDTO> getAllDishes(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Dish> dishes = dishRepository.findAll(pageable);
        return dishes.map(this::mapToDto);
    }

    private DishDTO mapToDto(Dish dish){
        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dish.getId());
        dishDTO.setName(dish.getName());
        dishDTO.setPrice(dish.getPrice());
        return dishDTO;
    }
    private Dish mapToEntity(DishRequest dishRequest){
        Dish dish =new Dish();
        dish.setName(dishRequest.getName());
        dish.setPrice(dishRequest.getPrice());
        return dish;
    }
    private DishRequest mapToDishRequest(Dish dish, List<DishIngredientDTO> ingredientDTOList) {
        return DishRequest.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .dishIngredientList(ingredientDTOList)
                .build();
    }
    private DishIngredientDTO maptoDishIngredientDTO(DishIngredient dishIngredient){
        DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
        dishIngredientDTO.setId(dishIngredient.getId());
        return dishIngredientDTO;
    }
}
