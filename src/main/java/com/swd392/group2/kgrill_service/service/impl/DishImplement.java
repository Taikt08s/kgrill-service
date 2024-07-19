package com.swd392.group2.kgrill_service.service.impl;


import com.swd392.group2.kgrill_model.model.*;
import com.swd392.group2.kgrill_model.repository.DishCategoryRepository;
import com.swd392.group2.kgrill_model.repository.DishIngredientRepository;
import com.swd392.group2.kgrill_model.repository.DishRepository;
import com.swd392.group2.kgrill_model.repository.IngredientRepository;
import com.swd392.group2.kgrill_service.dto.CategoryDTO;
import com.swd392.group2.kgrill_service.dto.DishDTO;
import com.swd392.group2.kgrill_service.dto.DishIngredientDTO;
import com.swd392.group2.kgrill_service.dto.request.DishRequest;
import com.swd392.group2.kgrill_service.exception.CategoryNotFoundException;
import com.swd392.group2.kgrill_service.exception.DishNotFoundException;
import com.swd392.group2.kgrill_service.exception.IngredientNotFoundException;
import com.swd392.group2.kgrill_service.service.DishService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishImplement implements DishService {
    private DishRepository dishRepository;
    private IngredientRepository ingredientRepository;
    private DishIngredientRepository dishIngredientRepository;
    private DishCategoryRepository dishCategoryRepository;

    @Autowired
    public DishImplement(DishRepository dishRepository, IngredientRepository ingredientRepository, DishIngredientRepository dishIngredientRepository, DishCategoryRepository dishCategoryRepository)
    {
        this.ingredientRepository = ingredientRepository;
        this.dishRepository=dishRepository;
        this.dishIngredientRepository= dishIngredientRepository;
        this.dishCategoryRepository = dishCategoryRepository;
    }

//    @Override
//    public List<Dish> findByNameAndPrice(String keyword) {
//
//    }

    @Override
    public void createDish(DishRequest dishRequest) {
        List<DishIngredientDTO> dishIngredientDtoList = dishRequest.getDishIngredientList();
        Dish dish = mapToEntity(dishRequest);
        List<DishIngredient> dishIngredientList = new ArrayList<>();
        DishCategory dc = dishCategoryRepository.findById(dishRequest.getCategory().getId()).orElseThrow(() -> new CategoryNotFoundException("Category could not be found"));
        for (DishIngredientDTO dishIngredientDTO : dishIngredientDtoList){
            Ingredient in = ingredientRepository.findById(dishIngredientDTO.getId()).orElseThrow(() -> new IngredientNotFoundException("Ingredient could not be found"));
            DishIngredient dishIngredient = new DishIngredient();
            dishIngredient.setDish(dish);
            dishIngredient.setIngredient(in);
            dishIngredientList.add(dishIngredient);
        }
        dish.setCategory(dc);
        dish.setDishIngredients(dishIngredientList);
        dishRepository.save(dish);
    }
    @Override
    @Transactional
    public DishRequest getDishByID(int id) {
        Dish dish = dishRepository.findById(id).orElseThrow(()->new DishNotFoundException("Dish could not be found"));
        List<DishIngredientDTO> dishIngredientDTOList = dish.getDishIngredients().stream().map(this::maptoDishIngredientDTO).toList();
        DishCategory dc = dishCategoryRepository.findById(dish.getCategory().getId()).orElseThrow(() -> new CategoryNotFoundException("Category could not be found"));
        CategoryDTO categoryDTO = mapToCategoryDTO(dc);
        return mapToDishRequest(dish, dishIngredientDTOList,categoryDTO);
    }

    @Override
    @Transactional
    public void updateDish(DishRequest dishRequest) {
        List<DishIngredientDTO> dishIngredientDtoList = dishRequest.getDishIngredientList();
        Dish updatedDish = mapToEntity(dishRequest);
        Dish existedDish = dishRepository.findById(dishRequest.getId()).orElseThrow(() -> new DishNotFoundException("Dish could not be found"));

        DishCategory dc = dishCategoryRepository.findById(dishRequest.getCategory().getId()).orElseThrow(() -> new CategoryNotFoundException("Category could not be found"));
        updatedDish.setCategory(dc);
        List<Integer> existedDishIngredientIdInList = existedDish.getDishIngredients()
                .stream()
                .map(DishIngredient -> DishIngredient.getIngredient().getId())
                .toList();

        List<Integer> newDishIngredientIdInList = dishIngredientDtoList
                .stream().map(DishIngredientDTO::getId)
                .toList();

        List<Integer> removeDishIngredientIdInList = existedDishIngredientIdInList.stream()
                .filter(existedDishIngredientId-> !newDishIngredientIdInList.contains(existedDishIngredientId))
                .toList();
        for (DishIngredientDTO dishIngredientDTO : dishIngredientDtoList) {
            int existedIngredientIdPosition = existedDishIngredientIdInList.indexOf(dishIngredientDTO.getId());
            if (existedIngredientIdPosition == -1) {
                Ingredient in = ingredientRepository.findById(dishIngredientDTO.getId()).orElseThrow(() -> new IngredientNotFoundException("Ingredient could not be found"));
                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setDish(updatedDish);
                dishIngredient.setIngredient(in);
                updatedDish.getDishIngredients().add(dishIngredient);
            } else {
                int existedIngredientId = existedDishIngredientIdInList.get(existedIngredientIdPosition);
                DishIngredient dishIngredient = dishIngredientRepository.findByIngredient_IdAndDish_Id(existedIngredientId, updatedDish.getId());
                updatedDish.getDishIngredients().add(dishIngredient);
            }
            for (Integer removeDishIngredientId : removeDishIngredientIdInList) {
                DishIngredient dishIngredient = dishIngredientRepository.findByIngredient_IdAndDish_Id(removeDishIngredientId, updatedDish.getId());
                dishIngredientRepository.delete(dishIngredient);
            }


            dishRepository.save(updatedDish);
        }


    }

    @Override
    public void deleteDish(int id) {
        Dish dish = dishRepository.findById(id).orElseThrow(()->new DishNotFoundException("Dish could not be found"));
        dishRepository.delete(dish);
    }

    @Override
    public Page<Dish> searchDishByFilter(int pageNumber, int pageSize, double minPrice, double maxPrice, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Dish> dishes= dishRepository.findByNameAndPrice(keyword, minPrice, maxPrice, pageable);
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
        return DishDTO.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .build();
    }
    private Dish mapToEntity(DishRequest dishRequest){
        return Dish.builder()
                .id(dishRequest.getId())
                .name(dishRequest.getName())
                .price(dishRequest.getPrice())
                .dishIngredients(new ArrayList<>())
                .build();
    }
    private CategoryDTO mapToCategoryDTO(DishCategory category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .category(category.getCategory())
                .build();
    }
    private DishRequest mapToDishRequest(Dish dish, List<DishIngredientDTO> ingredientDTOList, CategoryDTO categoryDTO) {
        return DishRequest.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .dishIngredientList(ingredientDTOList)
                .category(categoryDTO)
                .build();
    }
    private DishIngredientDTO maptoDishIngredientDTO(DishIngredient dishIngredient){
        DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
        dishIngredientDTO.setId(dishIngredient.getIngredient().getId());
        dishIngredientDTO.setName(dishIngredient.getIngredient().getName());
        return dishIngredientDTO;
    }

}
