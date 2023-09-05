package com.ecommerce.library.repository;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT * FROM categories WHERE is_activated = true and is_deleted = false", nativeQuery = true)
    List<Category> findAllByActivated();

    /*CUSTOMER*/
    @Query("select new com.ecommerce.library.dto.CategoryDto(c.id, c.name, count(p.category.id)) from Category c inner join Product p on p.category.id = c.id where c.is_activated = true and c.is_deleted = false group by c.id")
    List<CategoryDto> getCategoryAndProducts();
}
