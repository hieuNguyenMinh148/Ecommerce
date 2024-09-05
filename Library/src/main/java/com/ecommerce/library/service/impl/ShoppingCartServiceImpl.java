package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CartItemRepository;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ShoppingCart addItemToCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();

        if (cart == null) {
            cart = new ShoppingCart();
        }
        Set<CartItem> cartItems = cart.getCartItem();
        Product productUpdateQuantity = productRepository.getById(product.getId());
        productUpdateQuantity.setCurrentQuantity(productUpdateQuantity.getCurrentQuantity() - quantity);
        productRepository.save(productUpdateQuantity);

        CartItem cartItem = findCartItem(cartItems, product.getId());
        if (cartItems == null) {
            cartItems = new HashSet<>();
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                if (product.getDiscountedPrice() != 0) {
                    cartItem.setTotal(quantity * product.getCostPrice());
                    cartItem.setTotalPrice(quantity * product.getDiscountedPrice());
                    cartItem.setDiscountFee((product.getCostPrice() - product.getDiscountedPrice()) * quantity);
                } else {
                    cartItem.setTotalPrice(quantity * product.getCostPrice());
                    cartItem.setTotal(quantity * product.getCostPrice());
                }
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
            }
        } else {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                if (product.getDiscountedPrice() != 0) {
                    cartItem.setTotal(quantity * product.getCostPrice());
                    cartItem.setTotalPrice(quantity * product.getDiscountedPrice());
                    cartItem.setDiscountFee((product.getCostPrice() - product.getDiscountedPrice()) * quantity);
                } else {
                    cartItem.setTotalPrice(quantity * product.getCostPrice());
                    cartItem.setTotal(quantity * product.getCostPrice());
                }
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                if (product.getDiscountedPrice() != 0) {
                    cartItem.setTotal(quantity * product.getCostPrice());
                    cartItem.setTotalPrice(quantity * product.getDiscountedPrice());
                    cartItem.setDiscountFee((product.getCostPrice() - product.getDiscountedPrice()) * quantity);
                } else {
                    cartItem.setTotalPrice(quantity * product.getCostPrice());
                    cartItem.setTotal(quantity * product.getCostPrice());
                }
                cartItemRepository.save(cartItem);
            }
        }
        cart.setCartItem(cartItems);

        int totalItems = totalItems(cart.getCartItem());
        double totalPrice = totalPrice(cart.getCartItem());
        double totalDiscountedFee = totalDiscountedFee(cart.getCartItem());
        double total = total(cart.getCartItem());

        cart.setTotal(total);
        cart.setDiscountFee(totalDiscountedFee);
        cart.setTotalPrices(totalPrice);
        cart.setTotalItems(totalItems);
        cart.setCustomer(customer);

        return shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCart updateItemInCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();

        Set<CartItem> cartItems = cart.getCartItem();

        CartItem item = findCartItem(cartItems, product.getId());
        int subtractedQuantity = quantity - item.getQuantity();
        item.setQuantity(quantity);

        Product productUpdateQuantity = productRepository.getById(product.getId());
        productUpdateQuantity.setCurrentQuantity(productUpdateQuantity.getCurrentQuantity() - subtractedQuantity);
        productRepository.save(productUpdateQuantity);
        if (product.getDiscountedPrice() != 0) {
            item.setTotal(quantity * product.getCostPrice());
            item.setTotalPrice(quantity * product.getDiscountedPrice());
            item.setDiscountFee((product.getCostPrice() - product.getDiscountedPrice()) * quantity);
        } else {
            item.setTotalPrice(quantity * product.getCostPrice());
            item.setTotal(quantity * product.getCostPrice());
        }

        cartItemRepository.save(item);

        int totalItems = totalItems(cartItems);
        double totalPrice = totalPrice(cartItems);
        double totalDiscountedFee = totalDiscountedFee(cart.getCartItem());
        double total = total(cart.getCartItem());

        cart.setTotal(total);
        cart.setDiscountFee(totalDiscountedFee);
        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCart deleteItemFromCart(Product product, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();

        Set<CartItem> cartItems = cart.getCartItem();

        CartItem item = findCartItem(cartItems, product.getId());

        Product productProductUpdateQuantity = productRepository.getById(product.getId());
        productProductUpdateQuantity.setCurrentQuantity(productProductUpdateQuantity.getCurrentQuantity() + item.getQuantity());
        productRepository.save(productProductUpdateQuantity);

        cartItems.remove(item);

        cartItemRepository.delete(item);

        double totalPrice = totalPrice(cartItems);
        int totalItems = totalItems(cartItems);
        double totalDiscountedFee = totalDiscountedFee(cart.getCartItem());
        double total = total(cart.getCartItem());

        cart.setTotal(total);
        cart.setDiscountFee(totalDiscountedFee);
        cart.setCartItem(cartItems);
        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return shoppingCartRepository.save(cart);
    }

    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private int totalItems(Set<CartItem> cartItems) {
        int totalItems = 0;
        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
        }
        return totalItems;
    }

    private double totalPrice(Set<CartItem> cartItems) {
        double totalPrice = 0.0;

        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
        }

        return totalPrice;
    }

    private double total(Set<CartItem> cartItems) {
        double total = 0.0;

        for (CartItem item : cartItems) {
            total += item.getTotal();
        }

        return total;
    }

    private double totalDiscountedFee(Set<CartItem> cartItems) {
        double totalDiscountedFee = 0.0;

        for (CartItem item : cartItems) {
            totalDiscountedFee += item.getDiscountFee();
        }

        return totalDiscountedFee;
    }
}
