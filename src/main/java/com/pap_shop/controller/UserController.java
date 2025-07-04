package com.pap_shop.controller;

import com.pap_shop.dto.LoginRequest;
import com.pap_shop.dto.UserRequest;
import com.pap_shop.entity.User;
import com.pap_shop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update")
    public ResponseEntity<UserRequest> updateUser(@RequestBody User updatedUser) {
        User user = userService.updateUser(updatedUser);
        UserRequest userRequest = new UserRequest(user.getId(),
                                                    user.getName(),
                                                    user.getEmail(),
                                                    user.getPhone(),
                                                    user.getAddress(),user.getRole().getRoleId());
        return ResponseEntity.ok(userRequest);
    }

    /**
     * Register a new customer.
     *
     * @param user the customer object containing registration details
     * @return a success message
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.register(user);
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
        return userService.login(request.getEmailOrPhoneOrUsername(), request.getPassword());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer ID){
        Optional<User> user = userService.getUserById(ID);
        return user.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAllUsers(){ return userService.getAllUsers();}
}
