package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.utils.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageUpload imageUpload;

    /*ADMIN*/
    private List<ProductDto> transfer(List<Product> products) {
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setDescription(product.getDescription());
            productDto.setCategory(product.getCategory());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setDiscountedPrice(product.getDiscountedPrice());
            productDto.setImage(product.getImage());
            productDto.setDelete(product.is_deleted());
            productDto.setActivated(product.is_activated());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }
/*CUSTOMER*/
    private List<ProductDto> transferActivated(List<Product> products) {
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : products) {
            if (product.is_activated()==true){
                ProductDto productDto = new ProductDto();
                productDto.setId(product.getId());
                productDto.setName(product.getName());
                productDto.setDescription(product.getDescription());
                productDto.setCategory(product.getCategory());
                productDto.setCostPrice(product.getCostPrice());
                productDto.setSalePrice(product.getSalePrice());
                productDto.setDiscountedPrice(product.getDiscountedPrice());
                productDto.setImage(product.getImage());
                productDto.setDelete(product.is_deleted());
                productDto.setActivated(product.is_activated());
                productDto.setCurrentQuantity(product.getCurrentQuantity());
                productDtoList.add(productDto);
            }
        }
        return productDtoList;
    }

    @Override
    public Product save(MultipartFile imageProduct, ProductDto productDto) {
        try {
            Product product = new Product();
            if (imageProduct == null) {
                product.setImage(null);
            } else {
                if (imageUpload.uploadImage(imageProduct)) {
                    System.out.println("Upload done");
                }
                product.setImage(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
            }
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCategory(productDto.getCategory());
            product.setCostPrice(productDto.getCostPrice());
            product.setDiscountedPrice(productDto.getCostPrice() - (productDto.getCostPrice() * productDto.getSalePrice() / 100));
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.set_activated(true);
            product.set_deleted(false);
            product.setDate_created(new Date());
            product.setDate_updated(new Date());
            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to upload image");
            return null;
        }
    }

    @Override
    public Product update(MultipartFile imageProduct, ProductDto productDto) {
        try {
            Product product = productRepository.getById(productDto.getId());
            if (imageProduct.isEmpty()) {
                System.out.println("null");
                product.setImage(product.getImage());
            } else {
                if (imageUpload.checkExisted(imageProduct) == false) {
                    imageUpload.uploadImage(imageProduct);
                }
                product.setImage(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
            }
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCostPrice(productDto.getCostPrice());
            product.setCategory(productDto.getCategory());
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.setSalePrice(productDto.getSalePrice());
            product.setDiscountedPrice(productDto.getCostPrice() - (productDto.getCostPrice() * productDto.getSalePrice() / 100));
            product.setDate_updated(new Date());
            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productRepository.getById(id);
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setCategory(product.getCategory());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setImage(product.getImage());
        productDto.setDelete(product.is_deleted());
        productDto.setActivated(product.is_activated());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        return productDto;
    }

    private Page toPage(List<ProductDto> list, Pageable pageable) {
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size()) ? list.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, list.size());
    }

    @Override
    public List<ProductDto> findAll() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtoList = transfer(products);
        return productDtoList;
    }

    @Override
    public Page<ProductDto> pageProduct(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        List<ProductDto> products = transfer(productRepository.findAllByDateCreated());
        Page<ProductDto> productPage = toPage(products, pageable);
        return productPage;
    }

    @Override
    public Page<ProductDto> searchProducts(int pageNo, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        List<ProductDto> productDtoList = transfer(productRepository.searchProductsList(keyword));
        Page<ProductDto> products = toPage(productDtoList, pageable);
        return products;
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepository.getById(id);
        product.set_deleted(true);
        product.set_activated(false);
        productRepository.save(product);
    }


    @Override
    public void enableById(Long id) {
        Product product = productRepository.getById(id);
        product.set_activated(true);
        product.set_deleted(false);
        productRepository.save(product);
    }

    @Override
    public List<Product> getListForExport() {
        return productRepository.getProductsByQuantityAndActivated();
    }


    /*CUSTOMER*/
    @Override
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.getById(id);
    }

    @Override
    public List<Product> getRelatedProducts(Long categoryId) {
        return productRepository.getRelatedProducts(categoryId);
    }

    @Override
    public List<Product> getProductInCategory(Long categoryId) {
        return productRepository.getProductInCategory(categoryId);
    }

    @Override
    public List<ProductDto> findAllActivated() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtoList = transferActivated(products);
        return productDtoList;
    }

    @Override
    public List<ProductDto> findNewActivated() {
        List<Product> products = productRepository.findTop5ByActivated();
        List<ProductDto> productDtoList = transferActivated(products);
        return productDtoList;
    }

    @Override
    public List<Product> filterHighPrice() {
        return productRepository.filterHighPrice();
    }

    @Override
    public List<Product> filterLowPrice() {
        return productRepository.filterLowPrice();
    }

    @Override
    public Page<Product> getPageProducts(Long categoryId, int pageNumber, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date_created");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return productRepository.pageProduct(pageRequest, categoryId);
    }

    @Override
    public Page<Product> searchPageProducts(Long categoryId, int pageNumber, int pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date_created");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return productRepository.searchPageProduct(pageRequest, categoryId, keyword);
    }


}
