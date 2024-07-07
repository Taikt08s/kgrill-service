package com.swd392.group2.kgrill_service.service.impl;


import com.swd392.group2.kgrill_model.model.Dish;
import com.swd392.group2.kgrill_model.repository.DishRepository;
import com.swd392.group2.kgrill_service.dto.DishDTO;
import com.swd392.group2.kgrill_service.exception.DishNotFoundException;
import com.swd392.group2.kgrill_service.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class DishImplement implements DishService {
    private DishRepository dishRepository;
    @Autowired
    public DishImplement(DishRepository dishRepository){
        this.dishRepository=dishRepository;
    }

//    @Override
//    public List<Dish> findByNameAndPrice(String keyword) {
//
//    }

    @Override
    public DishDTO createDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        dish.setName(dishDTO.getName());
        dish.setPrice(dishDTO.getPrice());
//        dish.setCategory(dishDTO.getCategory());
//        dish.setDishIngredients(dishDTO.getDishIngredients());
//        dish.setPackageDishes(dishDTO.getPackageDishes());

        Dish newDish = dishRepository.save(dish);
        DishDTO dishResponse = new DishDTO();
        dishResponse.setId(newDish.getId());
        dishResponse.setName(newDish.getName());
        dishResponse.setPrice(newDish.getPrice());
//        dishResponse.setCategory(newDish.getCategory());
//        dishResponse.setDishIngredients(newDish.getDishIngredients());
//        dishResponse.setPackageDishes(newDish.getPackageDishes());
        return dishResponse;
    }

//    @Override
//    public List<DishDTO> getAllDish() {
//        List<Dish> dishes = dishRepository.findAll();
//        return dishes.stream().map(d -> mapToDto(d)).collect(Collectors.toList());
//    }

    @Override
    public DishDTO getDishByID(int id) {
        Dish dish = dishRepository.findById(id).orElseThrow(()->new DishNotFoundException("Dish could not be found"));
        return mapToDto(dish);
    }

    @Override
    public DishDTO updateDish(DishDTO dishDTO, int id) {

        Dish dish = dishRepository.findById(id).orElseThrow(()->new DishNotFoundException("Dish could not be found"));


        dish.setName(dishDTO.getName());
        dish.setPrice(dishDTO.getPrice());


        dish.setId(dishDTO.getId());
        dish.setName(dishDTO.getName());

        dish.setPrice(dishDTO.getPrice());


        Dish updatedDish= dishRepository.save(dish);
        return mapToDto(updatedDish);



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
    private Dish mapToEntity(DishDTO dishDTO){
        Dish dish =new Dish();
        dish.setName(dishDTO.getName());

        dish.setPrice(dishDTO.getPrice());

        return dish;
    }
}
