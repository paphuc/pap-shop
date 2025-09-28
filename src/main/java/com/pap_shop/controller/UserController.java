package com.pap_shop.controller;

import com.pap_shop.dto.ChangePasswordRequest;
import com.pap_shop.dto.ForgotPasswordRequest;
import com.pap_shop.dto.LoginRequest;
import com.pap_shop.dto.ResetPasswordRequest;
import com.pap_shop.dto.UserRequest;
import com.pap_shop.dto.ValidateCodeRequest;
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

    /**
     * Update user information.
     *
     * @param updatedUser the user object containing updated information
     * @return the updated user information
     */
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

    @GetMapping("/profile")
    public ResponseEntity<UserRequest> getProfile() {
        User user = userService.getCurrentUser();
        UserRequest userRequest = new UserRequest(user.getId(), user.getName(),
                user.getEmail(), user.getPhone(), user.getAddress(), user.getRole().getRoleId());
        return ResponseEntity.ok(userRequest);
    }

    /**
     * Get user by ID.
     *
     * @param ID the user ID
     * @return the user if found, otherwise 404
     */

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer ID){
        Optional<User> user = userService.getUserById(ID);
        return user.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    /**
     * Get all users.
     *
     * @return list of all users
     */
    @GetMapping
    public List<User> getAllUsers(){ return userService.getAllUsers();}

    /**
     * Logout user by invalidating JWT token.
     *
     * @param request the HTTP request containing Authorization header
     * @return success message
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
        return ResponseEntity.ok("Password reset code sent to your email");
    }

    /**
     * Validates reset code.
     *
     * @param request the validate code request containing reset code
     * @return success message
     */
    @PostMapping("/validate-reset-code")
    public ResponseEntity<String> validateResetCode(@RequestBody ValidateCodeRequest request) {
        userService.validateResetCode(request.getCode());
        return ResponseEntity.ok("Reset code is valid");
    }

    /**
     * Resets user password using reset code.
     *
     * @param request the reset password request containing code and new password
     * @return success message
     */
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    // Admin endpoints
    /**
     * Create a new user (Admin only).
     *
     * @param user the user object to create
     * @return the created user
     */
    @PostMapping("/admin/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.addUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Update user role (Admin only).
     *
     * @param id the user ID
     * @param roleId the new role ID
     * @return the updated user
     */
    @PutMapping("/admin/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Integer id, @RequestBody Integer roleId) {
        User updatedUser = userService.updateUserRole(id, roleId);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Toggle user status (Admin only).
     *
     * @param id the user ID
     * @return the updated user
     */
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Integer id) {
        User updatedUser = userService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user (Admin only).
     *
     * @param id the user ID to delete
     * @return success message
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
