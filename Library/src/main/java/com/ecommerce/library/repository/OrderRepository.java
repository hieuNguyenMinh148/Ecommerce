package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT * from orders ORDER BY order_date", nativeQuery = true)
    Page<Order> findAllOrder(Pageable pageable);

    @Query(value = "SELECT * FROM orders WHERE order_status LIKE %:status% ORDER BY order_date", nativeQuery = true)
    Page<Order> searchAllOrder(Pageable pageable, @Param("status") String status);
}
