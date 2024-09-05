package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.customer.service.PaypalService;
import com.ecommerce.library.service.OrderService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    private final PaypalService paypalService;
    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping("/payment/create")
    public RedirectView createPayment(Principal principal) {
        try {
            String username = principal.getName();
            Customer customer = customerService.findByUsername(username);
            ShoppingCart cart = customer.getShoppingCart();

            String cancelUrl = "http://localhost:8090/shop/payment/cancel";
            String successUrl = "http://localhost:8090/shop/payment/success";
            Payment payment = paypalService.createPayment(Double.valueOf(cart.getTotal()), "USD", "Paypal", "sale", "description", cancelUrl, successUrl);

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error("Error occurred:: ", e);
        }
        return new RedirectView("/payment/error");
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(Principal principal, @RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                String username = principal.getName();
                Customer customer = customerService.findByUsername(username);
                ShoppingCart cart = customer.getShoppingCart();
                orderService.saveOrder(cart, "Paypal");
                return "paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            log.error("Error occurred:: ", e);
        }
        return "paymentSuccess";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(RedirectAttributes attributes) {
        attributes.addFlashAttribute("message", "Payment cancelled");
        return "redirect:/check-out";
    }

    @GetMapping("/payment/error")
    public String paymentError() {
        return "paymentError";
    }
}
