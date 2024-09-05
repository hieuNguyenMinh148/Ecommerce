package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    //    @GetMapping("/products")
    //    public String products(Model model) {
    //        List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
    //        List<Product> products = productService.getAllProducts();
    //        model.addAttribute("products", products);
    //        model.addAttribute("categories", categoryDtoList);
    //        return "shop-detail";
    //    }

    @GetMapping("/product-detail/{id}")
    public String findProductById(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        Long categoryId = product.getCategory().getId();
        List<Product> products = productService.getRelatedProducts(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("product", product);
        model.addAttribute("title", "product detail");
        return "product-detail";
    }

    @GetMapping("/product-of-category/{id}")
    public String getProductInCategory(@PathVariable("id") Long categoryId, Model model,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(required = false, name = "keyword") String keyword) {
        Category category = categoryService.findById(categoryId);
        int currentPage = page > 1 ? page : 1;
        List<CategoryDto> categoryDto = categoryService.getCategoryAndProduct();
        int totalPages = 0;
        if (keyword != null && !keyword.equals("")) {
            Page<Product> products = productService.searchPageProducts(categoryId, currentPage - 1, size, keyword);
            totalPages = products.getTotalPages();
            model.addAttribute("keyword", keyword);
            model.addAttribute("products", products);
        } else {
            Page<Product> products = productService.getPageProducts(categoryId, currentPage - 1, size);
            totalPages = products.getTotalPages();
            model.addAttribute("products", products);
        }

        model.addAttribute("category", category);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
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
