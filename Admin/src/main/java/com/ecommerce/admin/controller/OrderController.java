package com.ecommerce.admin.controller;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.service.OrderDetailService;
import com.ecommerce.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/orders")
    public String getOrderPage(Model model, Principal principal,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(defaultValue = "") String status) {
        if (principal == null) {
            return "redirect:/login";
        }
//        List<Order> orders = orderService.getAllOrders();
        int currentPage = page > 1 ? page : 1;
        Page<Order> orders = orderService.getOrderPage(currentPage - 1, size, status);
        int totalPages = orders.getTotalPages();
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/order-detail/{id}")
    public String getOrderDetailPage(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "order-detail";
    }

    @GetMapping("/order-detail/cancel-order/{id}")
    public String cancelOrder(@PathVariable("id") Long id, RedirectAttributes attributes) {
        orderService.cancelOrder(id);
        attributes.addFlashAttribute("success", "Cancel successfully");
        return "redirect:/orders";
    }

    @GetMapping("/order-detail/accept-order/{id}")
    public String acceptOrder(@PathVariable("id") Long id, RedirectAttributes attributes) {
        orderService.acceptOrder(id);
        attributes.addFlashAttribute("success", "Cancel successfully");
        return "redirect:/orders";
    }

    @GetMapping("/delete-order/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes attributes) {
        orderService.deleteOrder(id);
        attributes.addFlashAttribute("success", "Delete successfully");
        return "redirect:/orders";
    }

}
