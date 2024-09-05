package com.ecommerce.library.repository;

import com.ecommerce.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /*ADMIN*/

    @Query(value = "SELECT * FROM products ORDER BY date_created DESC",nativeQuery = true)
    List<Product> findAllByDateCreated();

    @Query(value = "SELECT * FROM products p1 " +
            "WHERE is_activated = true AND is_deleted = false AND " +
            "(SELECT COUNT(*) FROM products p2 " +
            "WHERE p2.category_id = p1.category_id " +
            "AND p2.is_activated = true AND p2.is_deleted = false " +
            "AND p2.date_updated > p1.date_updated) < 4 " +
            "ORDER BY p1.category_id, p1.date_updated DESC", nativeQuery = true)
    List<Product> findTop5ByActivated();


    @Query(value = "SELECT * FROM products WHERE is_activated = true and is_deleted = false ORDER BY date_updated DESC", nativeQuery = true)
    List<Product> findAllByActivated();

    @Query(value = "SELECT * FROM products WHERE description like %?1% or name like %?1%", nativeQuery = true)
    Page<Product> searchProducts(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE description like %?1% or name like %?1%", nativeQuery = true)
    List<Product> searchProductsList(String keyword);

    @Query(value = "SELECT p FROM Product p WHERE p.is_activated = true and p.is_deleted = false ORDER BY p.currentQuantity ASC, p.date_updated")
    List<Product> getProductsByQuantityAndActivated();

    /*CUSTOMER*/
    @Query(value = "SELECT * FROM products WHERE category_id = :categoryId AND is_activated = true AND is_deleted = false ORDER BY date_created DESC", nativeQuery = true)
    Page<Product> pageProduct(Pageable pageable, @Param("categoryId") Long categoryId);

    @Query(value = "SELECT * FROM products WHERE category_id = :categoryId AND name LIKE %:keyword% AND is_activated = true AND is_deleted = false ORDER BY date_created DESC", nativeQuery = true)
    Page<Product> searchPageProduct(Pageable pageable, @Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Query(value ="SELECT * FROM products WHERE is_activated = true and is_deleted = false", nativeQuery = true)
    List<Product> getAllProducts();

    @Query(value = "SELECT * FROM products WHERE category_id = ?1 and is_activated = true and is_deleted = false", nativeQuery = true)
    List<Product> getRelatedProducts(Long categoryId);

    @Query(value = "SELECT * FROM products p INNER JOIN categories c on c.category_id = p.category_id WHERE c.category_id =?1 AND p.is_deleted = false AND p.is_activated = true ORDER BY p.cost_price DESC", nativeQuery = true)
    List<Product> getProductInCategory(Long categoryId);

    @Query(value = "SELECT * FROM products  WHERE is_activated = true AND is_deleted = false order by cost_price DESC",nativeQuery = true)
    List<Product> filterHighPrice();

    @Query(value = "SELECT * FROM products WHERE is_activated = true AND is_deleted = false order by cost_price",nativeQuery = true)
    List<Product> filterLowPrice();
}
