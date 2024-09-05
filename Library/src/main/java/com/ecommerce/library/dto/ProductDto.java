package com.ecommerce.library.dto;

import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double costPrice;
    private double salePrice;
    private double discountedPrice;
    private int currentQuantity;
    private Category category;
    private String image;
    private boolean activated;
    private boolean delete;

}
