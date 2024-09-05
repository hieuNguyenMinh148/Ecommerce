package com.ecommerce.customer.controller.api;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductRestController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/high-price")
    public ResponseEntity<?> filterHighPrice() {
        List<Category> categories = categoryService.findAllByActivated();
        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
        List<Product> products = productService.filterHighPrice();
        return new ResponseEntity<>(products, HttpStatus.ACCEPTED);
    }

    @GetMapping("/low-price")
    public ResponseEntity<?> filterLowPrice() {
        List<Category> categories = categoryService.findAllByActivated();
        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
        List<Product> products = productService.filterLowPrice();
        return new ResponseEntity<>(products, HttpStatus.ACCEPTED);
    }

    @GetMapping("/getByPage")
    public ResponseEntity<?> getByPage(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "1") Long categoryId,
                                       @RequestParam(required = false, name = "keyword") String keyword) {
        int currentPage = page > 1 ? page : 1;
        return new ResponseEntity<>(productService.getPageProducts(categoryId,currentPage - 1, size), HttpStatus.OK);
    }
}
