package com.swd392.group2.kgrill_service.service;



import com.swd392.group2.kgrill_service.dto.DishDTO;

import java.util.List;

public interface DishService {
    DishDTO createDish(DishDTO dishDTO);
    List<DishDTO> getAllDish();
    DishDTO getDishByID(int id);
    DishDTO updateDish(DishDTO dishDTO, int id);
    void deleteDish(int id);
}
