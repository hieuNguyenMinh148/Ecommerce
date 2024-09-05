package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("title", "Login");
        return "login";
    }

    @RequestMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("title", "Home Page");
        List<ProductDto> products = productService.findAll();
        int countProduct = 0;
        for (ProductDto product : products) {
            if (product.isActivated()) {
                countProduct++;
            }
        }
        model.addAttribute("countProduct", countProduct);

        List<Order> orders = orderService.getAllOrders();
        int countOrder = 0;
        int totalOrder = 0;
        for (Order order : orders) {
            countOrder++;
            if (order.isPaid()){
                totalOrder += order.getTotal();
            }
        }
        model.addAttribute("countOrder", countOrder);
        model.addAttribute("totalOrder", totalOrder);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/index")
    public String getIndex(Model model) {
        model.addAttribute("title", "Home Page");
        List<ProductDto> products = productService.findAll();
        int countProduct = 0;
        for (ProductDto product : products) {
            countProduct++;
        }
        model.addAttribute("countProduct", countProduct);

        List<Order> orders = orderService.getAllOrders();
        int countOrder = 0;
        int totalOrder = 0;
        for (Order order : orders) {
            countOrder++;
            if (order.isPaid()){
                totalOrder += order.getTotal();
            }
        }
        model.addAttribute("countOrder", countOrder);
        model.addAttribute("totalOrder", totalOrder);
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto, BindingResult result, Model model) {
        try {

            if (result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                result.toString();
                return "register";
            }
            String username = adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);
            if (admin != null) {
                model.addAttribute("adminDto", adminDto);
                System.out.println("admin not null");
                model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if (adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                model.addAttribute("success", "Register successfully!");
                model.addAttribute("adminDto", adminDto);
            } else {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
                System.out.println("password not same");
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "The server has been wrong!");
        }
        return "register";

    }


}
