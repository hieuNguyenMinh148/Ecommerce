package com.ecommerce.library.service;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    /*ADMIN*/
    List<ProductDto> findAll();

    Product save(MultipartFile imageProduct, ProductDto productDto);

    Product update(MultipartFile imageProduct, ProductDto productDto);

    ProductDto getById(Long id);

    Page<ProductDto> pageProduct(int pageNo);

    Page<ProductDto> searchProducts(int pageNo, String keyword);

    void deleteById(Long id);

    void enableById(Long id);

    List<Product> getListForExport();


    /*CUSTOMER*/
    List<Product> getAllProducts();

    Product getProductById(Long id);

    List<Product> getRelatedProducts(Long categoryId);

    List<Product> getProductInCategory(Long categoryId);

    List<ProductDto> findAllActivated();

    List<ProductDto> findNewActivated();

    List<Product> filterHighPrice();

    List<Product> filterLowPrice();

    Page<Product> getPageProducts(Long categoryId, int pageNumber, int pageSize);

    Page<Product> searchPageProducts(Long categoryId, int pageNumber, int pageSize, String keyword);
}
