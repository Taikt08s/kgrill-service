package com.swd392.group2.kgrill_service.service.impl;


import com.swd392.group2.kgrill_model.model.Dish;
import com.swd392.group2.kgrill_model.model.Ingredient;
import com.swd392.group2.kgrill_model.repository.IngredientRepository;
import com.swd392.group2.kgrill_service.dto.IngredientDTO;
import com.swd392.group2.kgrill_service.exception.IngredientNotFoundException;
import com.swd392.group2.kgrill_service.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientImplement implements IngredientService {
    private IngredientRepository ingredientRepository;
    @Autowired
    public IngredientImplement(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO)
    {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDTO.getName());

        Ingredient newIn = ingredientRepository.save(ingredient);
        IngredientDTO ingredientResponse = new IngredientDTO();
        ingredientResponse.setId(newIn.getId());
        ingredientResponse.setName(newIn.getName());
        return ingredientResponse;
    }

//    @Override
//    public List<IngredientDTO> getAllIngredient() {
//        List<Ingredient> ins = ingredientRepository.findAll();
//        return ins.stream().map(d -> mapToDto(d)).collect(Collectors.toList());
//    }

    @Override
    public IngredientDTO getIngredientByID(int id)
    {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(()->new IngredientNotFoundException("Ingredient could not be found"));
        return mapToDto(ingredient);

    }

    @Override
    public IngredientDTO updateIngredient(IngredientDTO ingredientDTO, int id)
    {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(()->new IngredientNotFoundException("Ingredient could not be found"));
        ingredient.setId(ingredientDTO.getId());
        ingredient.setName(ingredientDTO.getName());
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return mapToDto(updatedIngredient);

    }

    @Override
    public void deleteIngredient(int id)
    {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(()->new IngredientNotFoundException("Ingredient could not be found"));
        ingredientRepository.delete(ingredient);
    }

    @Override
    public Page<IngredientDTO> searchIngredientByFilter(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return null;
    }

    @Override
    public Page<IngredientDTO> getAllIngredients(int pageNumber, int pageSize, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Ingredient> ins = ingredientRepository.findAll(pageable);
        return ins.map(this::mapToDto);
    }

    private IngredientDTO mapToDto(Ingredient ingredient){
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(ingredient.getId());
        ingredientDTO.setName(ingredient.getName());

        return ingredientDTO;
    }
    private Ingredient mapToEntity(IngredientDTO ingredientDTO){
        Ingredient ingredient =new Ingredient();
        ingredient.setName(ingredientDTO.getName());
        return ingredient;
    }
}
