package com.ecommerce.customer.controller;

import com.ecommerce.library.model.*;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.OrderDetailService;
import com.ecommerce.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/check-out")
    public String checkOut(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        if (customer.getPhoneNumber() == null || customer.getAddress() == null ||
                customer.getCity() == null || customer.getCountry() == null) {
            model.addAttribute("customer", customer);
            model.addAttribute("error", "You must fill all of the information after checkout");
            return "my-account1";
        } else {
            model.addAttribute("customer", customer);
            ShoppingCart cart = customer.getShoppingCart();
            model.addAttribute("cart", cart);
        }

        return "checkout";

    }

    @GetMapping("/order")
    public String order(Model model, Principal principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();
        List<Order> orderList = customer.getOrders();
        if (orderList.isEmpty()) {
            model.addAttribute("error", "You currently have no orders");
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
        }
        model.addAttribute("orders", orderList);
        session.setAttribute("totalItems", shoppingCart.getTotalItems());
        return "order";
    }

    @GetMapping("/save-order")
    public String saveOrder(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = customer.getShoppingCart();
        if (cart.getTotalItems() == 0 || cart.getTotalPrices() == 0) {
            model.addAttribute("customer", customer);
            model.addAttribute("cart", cart);
            model.addAttribute("error", "Your cart is empty");
            return "checkout";
        }
        orderService.saveOrder(cart, "Cash");
        return "redirect:/order";
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
        return "redirect:/order";
    }

    @GetMapping("/order-detail/receive-order/{id}")
    public String receiveOrder(@PathVariable("id") Long id, RedirectAttributes attributes) {
        orderService.receiveOrder(id);
        attributes.addFlashAttribute("success", "Thanks for your order");
        return "redirect:/order";
    }
}
