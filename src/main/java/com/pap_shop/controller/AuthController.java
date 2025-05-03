package com.pap_shop.controller;

import com.pap_shop.dto.LoginRequest;
import com.pap_shop.entity.Customer;
import com.pap_shop.service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    /**
     * Constructor to inject UserService.
     *
     * @param userService the service that handles user-related operations
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new customer.
     *
     * @param customer the customer object containing registration details
     * @return a success message
     */
    @PostMapping("/register")
    public String register(@RequestBody Customer customer) {
        userService.register(customer);
        return "Registered successfully";
    }

    /**
     * Login an existing customer.
     *
     * @param request the login request containing email/phone and password
     * @return the generated JWT token if login is successful
     */
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userService.login(request.getEmailOrPhone(), request.getPassword());
    }
}
