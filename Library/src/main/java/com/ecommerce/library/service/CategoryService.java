package com.ecommerce.library.service;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    /*ADMIN*/
    List<Category> findAll();

    Category save(Category category);

    Category findById(Long id);

    Category update(Category category);

    void deleteById(Long id);

    void enableById(Long id);

    List<Category> findAllByActivated();


    /*CUSTOMER*/
    List<CategoryDto> getCategoryAndProduct();

}
