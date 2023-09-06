package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class CartController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            model.addAttribute("check", "No item in your cart");
        }
        model.addAttribute("shoppingCart", shoppingCart);
        return "cart";
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("id") Long productId, @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return "redirect:/login";
        }
        Product product = productService.getProductById(productId);
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        ShoppingCart cart = shoppingCartService.addItemToCart(product, quantity, customer);
        return "redirect:" + request.getHeader("Referer");
    }
}
