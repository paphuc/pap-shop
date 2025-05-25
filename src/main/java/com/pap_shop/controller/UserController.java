package com.pap_shop.controller;

import com.pap_shop.entity.User;
import com.pap_shop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        User user = userService.updateUser(updatedUser);
        return ResponseEntity.ok(user);
    }
}
