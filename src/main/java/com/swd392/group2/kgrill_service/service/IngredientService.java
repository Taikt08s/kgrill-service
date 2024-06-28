package com.swd392.group2.kgrill_service.service;


import com.swd392.group2.kgrill_service.dto.IngredientDTO;

import java.util.List;

public interface IngredientService {
    IngredientDTO createIngredient(IngredientDTO ingredientDTO);
        List<IngredientDTO> getAllIngredient();
    IngredientDTO getIngredientByID(int id);
    IngredientDTO updateIngredient(IngredientDTO ingredientDTO, int id);
    void deleteIngredient(int id);
}
