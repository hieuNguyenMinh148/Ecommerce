package com.ecommerce.library.service;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Page<Order> getOrderPage(int pageNumber, int pageSize, String status);

    void saveOrder(ShoppingCart cart, String paymentMethod);

    void acceptOrder(Long id);

    void receiveOrder(Long id);

    void cancelOrder(Long id);

    void deleteOrder(Long id);

    List<Order> getAllOrders();

    Order getOrderById(Long id);
}
