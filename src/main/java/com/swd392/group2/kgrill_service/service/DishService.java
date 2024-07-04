package com.swd392.group2.kgrill_service.service;



import com.swd392.group2.kgrill_service.dto.DishDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DishService {
    DishDTO createDish(DishDTO dishDTO);
    DishDTO getDishByID(int id);
    DishDTO updateDish(DishDTO dishDTO, int id);
    void deleteDish(int id);
    Page<DishDTO> searchDishByFilter(int pageNumber, int pageSize, String sortField, String sortDir);

    Page<DishDTO> getAllDishes(int pageNumber, int pageSize, String sortField, String sortDir);
}
