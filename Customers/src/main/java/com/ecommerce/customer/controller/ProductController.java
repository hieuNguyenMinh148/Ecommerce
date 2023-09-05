package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String products(Model model) {
        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryDtoList);
        return "shop-detail";
    }

    @GetMapping("/product-detail/{id}")
    public String findProductById(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        Long categoryId = product.getCategory().getId();
        List<Product> products = productService.getRelatedProducts(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/product-of-category/{id}")
    public String getProductInCategory(@PathVariable("id") Long categoryId, Model model) {
        Category category = categoryService.findById(categoryId);
        List<CategoryDto> categoryDto = categoryService.getCategoryAndProduct();
        List<Product> products = productService.getProductInCategory(categoryId);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryDto);
        return "product-of-category";
    }

//    @GetMapping("/high-price")
//    public String filterHighPrice(Model model) {
//        List<Category> categories = categoryService.findAllByActivated();
//        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
//        List<Product> products = productService.filterHighPrice();
//        model.addAttribute("products", products);
//        model.addAttribute("categories", categories);
//        model.addAttribute("categoryDtoList", categoryDtoList);
//        return "filter-high-price";
//    }
}
