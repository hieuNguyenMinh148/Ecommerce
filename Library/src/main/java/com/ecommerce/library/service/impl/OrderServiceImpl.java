package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.*;
import com.ecommerce.library.repository.*;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private ProductRepository productRepository;


    @Override
    public Page<Order> getOrderPage(int pageNumber, int pageSize, String status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "order_date");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        if (!status.equals("")) {
            return orderRepository.searchAllOrder(pageRequest, status);
        } else {
            return orderRepository.findAllOrder(pageRequest);
        }
    }

    @Override
    public void saveOrder(ShoppingCart cart, String paymentMethod) {
        Order order = new Order();
        //        order.setOrderStatus("PENDING");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());
        order.setCustomer(cart.getCustomer());
        order.setTotalPrice(cart.getTotalPrices());
        order.setTotal(cart.getTotal());
        order.setDiscountFee(cart.getDiscountFee());
        order.setShippingFee(0);
        order.setPaymentMethod(paymentMethod);

        if (paymentMethod.equals("Paypal")) {
            order.setPaid(true);
            Date deliveryDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deliveryDate);

            // Thêm 5 ngày vào ngày giao hàng
            calendar.add(Calendar.DAY_OF_YEAR, 5);
            Date newDeliveryDate = calendar.getTime();
            order.setDeliveryDate(newDeliveryDate);
        }
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderRepository.save(order);
        for (CartItem item : cart.getCartItem()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setProduct(item.getProduct());
            orderDetail.setUnitPrice(item.getProduct().getCostPrice());
            orderDetail.setTotalPrice(item.getTotalPrice());
            orderDetail.setUnitSale(item.getProduct().getSalePrice());
            orderDetail.setDiscountFee(item.getDiscountFee());
            orderDetailRepository.save(orderDetail);
            orderDetailList.add(orderDetail);
            cartItemRepository.delete(item);
        }
        order.setOrderDetailList(orderDetailList);
        cart.setCartItem(new HashSet<>());
        cart.setTotalItems(0);
        cart.setTotalPrices(0);
        cart.setTotal(0);
        shoppingCartRepository.save(cart);
        orderRepository.save(order);
    }

    @Override
    public void acceptOrder(Long id) {
        Order order = orderRepository.getById(id);
        if (!order.getPaymentMethod().equals("Paypal")) {
            order.setPaid(true);
            Date deliveryDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deliveryDate);

            // Thêm 5 ngày vào ngày giao hàng
            calendar.add(Calendar.DAY_OF_YEAR, 5);
            Date newDeliveryDate = calendar.getTime();
            order.setDeliveryDate(newDeliveryDate);
        }
        //        order.setOrderStatus("ACCEPTED");
        order.setOrderStatus(OrderStatus.ACCEPTED);
        order.setCanceled(false);
        orderRepository.save(order);

        Dashboard dashboard = new Dashboard();
        dashboard.setBuyer(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        dashboard.setUser(order.getCustomer().getUsername());
        dashboard.setPaymentPrice(order.getTotalPrice());
        if (order.getPaymentMethod().equals("Paypal")) {
            dashboard.setDatePaid(new Date());
        }
        dashboard.setPaymentMethod(order.getPaymentMethod());
        dashboard.setOrderId(id);
        dashboardRepository.save(dashboard);
    }

    @Override
    public void receiveOrder(Long id) {
        Order order = orderRepository.getById(id);

        if (order.getPaymentMethod().equals("Cash")) {
            order.setPaid(true);
        }
        //        order.setOrderStatus("RECEIVED");
        order.setOrderStatus(OrderStatus.RECEIVED);
        order.setCanceled(false);
        orderRepository.save(order);

        Dashboard dashboard = dashboardRepository.findByOrderId(id);
        if (order.getPaymentMethod().equals("Cash")) {
            dashboard.setDatePaid(new Date());
        }
        dashboardRepository.save(dashboard);
    }

    @Override
    public void cancelOrder(Long id) {
        Order orderCancel = orderRepository.getById(id);
        orderCancel.setCanceled(true);
        //        orderCancel.setOrderStatus("PENDING");
        orderCancel.setOrderStatus(OrderStatus.CANCELLED);
        orderCancel.setDeliveryDate(null);
        orderCancel.setDateUpdate(new Date());

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(id);
        for (OrderDetail orderDetail : orderDetails) {
            Product product = productRepository.getById(orderDetail.getProduct().getId());
            product.setCurrentQuantity(product.getCurrentQuantity() + orderDetail.getQuantity());
            productRepository.save(product);
        }
        orderRepository.save(orderCancel);
    }

    @Override
    public void deleteOrder(Long id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(id);
        Order order = orderRepository.getById(id);
        if (!order.getOrderStatus().equals("RECEIVED")) {
            for (OrderDetail orderDetail : orderDetails) {
                Product product = productRepository.getById(orderDetail.getProduct().getId());
                product.setCurrentQuantity(product.getCurrentQuantity() + orderDetail.getQuantity());
                productRepository.save(product);
            }
        }
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.getById(id);
    }
}
