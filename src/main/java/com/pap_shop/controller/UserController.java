package com.pap_shop.controller;

import com.pap_shop.dto.ChangePasswordRequest;
import com.pap_shop.dto.ForgotPasswordRequest;
import com.pap_shop.dto.LoginRequest;
import com.pap_shop.dto.ResetPasswordRequest;
import com.pap_shop.dto.UserRequest;
import com.pap_shop.entity.User;
import com.pap_shop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * Logout hiện tại người dùng bằng cách vô hiệu hóa JWT token.
     *
     * @param request HTTP request chứa Authorization header
     * @return 200 OK nếu thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        userService.logout(authHeader);
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Change authenticated user's password.
     *
     * @param request the change password request containing old password, new password and confirm password
     * @return success message if password is changed successfully
     */
    @PutMapping("/update/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getOldPassword(), request.getNewPassword(), request.getConfirmNewPassword());
        return ResponseEntity.ok("Password updated successfully");
    }

    /**
     * Initiates password reset process by sending reset email.
     *
     * @param request the forgot password request containing email
     * @return success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    /**
     * Resets user password using reset token.
     *
     * @param request the reset password request containing token and new password
     * @return success message
     */
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}
