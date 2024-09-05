package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Role;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.RoleRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.Arrays;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        Customer customer = new Customer();
        ShoppingCart shoppingCart = new ShoppingCart();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPassword(customerDto.getPassword());
        customer.setUsername(customerDto.getUsername());
        customer.setShoppingCart(shoppingCart);
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));

        shoppingCartRepository.save(shoppingCart);
        Customer customerSave = customerRepository.save(customer);
        shoppingCart.setCustomer(customer);
        shoppingCartRepository.save(shoppingCart);
        return mapperDto(customerSave);
    }

    @Override
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public Customer saveInformation(Customer customer) {
        Customer customerSave = customerRepository.findById(customer.getId()).get();
        customerSave.setAddress(customer.getAddress());
        customerSave.setCity(customer.getCity());
        customerSave.setFirstName(customer.getFirstName());
        customerSave.setLastName(customer.getLastName());
        customerSave.setPhoneNumber(customer.getPhoneNumber());
        customerSave.setCountry(customer.getCountry());
        return customerRepository.save(customerSave);
    }

    private CustomerDto mapperDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPassword(customer.getPassword());
        customerDto.setUsername(customer.getUsername());
        return customerDto;
    }
}
