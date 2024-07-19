package com.swd392.group2.kgrill_service.service;



import com.swd392.group2.kgrill_model.model.Dish;
import com.swd392.group2.kgrill_service.dto.CategoryDTO;
import com.swd392.group2.kgrill_service.dto.DishDTO;
import com.swd392.group2.kgrill_service.dto.request.DishRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DishService {

    void createDish(DishRequest dishRequest);
    DishRequest getDishByID(int id);
    void updateDish(DishRequest dishRequest);
    void deleteDish(int id);
    Page<Dish> searchDishByFilter(int pageNumber, int pageSize, double minPrice, double maxPrice, String sortField, String sortDir,String keyword, String category);

    Page<DishDTO> getAllDishes(int pageNumber, int pageSize, String sortField, String sortDir);

}
