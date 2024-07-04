package com.swd392.group2.kgrill_service.service;


import com.swd392.group2.kgrill_service.dto.IngredientDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IngredientService {
    IngredientDTO createIngredient(IngredientDTO ingredientDTO);

    IngredientDTO getIngredientByID(int id);
    IngredientDTO updateIngredient(IngredientDTO ingredientDTO, int id);
    void deleteIngredient(int id);
    Page<IngredientDTO> searchIngredientByFilter(int pageNumber, int pageSize, String sortField, String sortDir);

    Page<IngredientDTO> getAllIngredients(int pageNumber, int pageSize, String sortField, String sortDir);
}
