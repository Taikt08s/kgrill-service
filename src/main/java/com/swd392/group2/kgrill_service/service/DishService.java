package com.swd392.group2.kgrill_service.service;



import com.swd392.group2.kgrill_model.model.Dish;
import com.swd392.group2.kgrill_service.dto.DishDTO;
import com.swd392.group2.kgrill_service.dto.request.DishRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DishService {

    DishRequest createDish(DishRequest dishRequest);
    DishRequest getDishByID(int id);
    DishRequest updateDish(DishRequest dishRequest, int id);
    void deleteDish(int id);
    Page<Dish> searchDishByFilter(int pageNumber, int pageSize, String sortField, String sortDir,String keyword);

    Page<DishDTO> getAllDishes(int pageNumber, int pageSize, String sortField, String sortDir);
}
