package com.pap_shop.service;

import com.pap_shop.entity.InvalidatedToken;
import com.pap_shop.entity.PasswordResetToken;
import com.pap_shop.entity.Roles;
import com.pap_shop.entity.User;
import com.pap_shop.exception.AuthenticationException;
import com.pap_shop.exception.DuplicateResourceException;
import com.pap_shop.exception.ResourceNotFoundException;
import com.pap_shop.repository.PasswordResetTokenRepository;
import com.pap_shop.repository.InvalidatedTokenRepository;
import com.pap_shop.repository.RoleRepository;
import com.pap_shop.repository.UserRepository;
import com.pap_shop.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    BCryptPasswordEncoder passwordEncoder;
    EmailService emailService;
    PasswordResetTokenRepository passwordResetTokenRepository;
    InvalidatedTokenRepository invalidatedTokenRepo;
    JwtDecoder jwtDecoder;


    /**
     *Register a new customer by encoding their password before saving to database.
     *
     * @param user the customer to be registered
     */
    public void register(User user) {

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (user.getEmail() == null || !user.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already in use");
        }

        if (user.getPhone() == null || !user.getPhone().matches("^\\d{9,15}$")) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Phone already in use");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Roles role = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);
        userRepository.save(user);
    }


    /**
     * Logs in a user by checking their email or phone and matching the password.
     * If successful, a JWT token is generated.
     *
     * @param emailOrPhoneOrUsername the email or phone number for login
     * @param rawPassword the raw password entered by the user
     * @return a JWT token if the login is successful
     * @throws RuntimeException if the user is not found or credentials are invalid
     */
    public String login(String emailOrPhoneOrUsername, String rawPassword) {
        Optional<User> optionalUser;

        if (emailOrPhoneOrUsername.contains("@")) {
            optionalUser = userRepository.findByEmail(emailOrPhoneOrUsername);
        } else if (emailOrPhoneOrUsername.matches("\\d+")) {
            optionalUser = userRepository.findByPhone(emailOrPhoneOrUsername);
        } else {
            optionalUser = userRepository.findByUsername(emailOrPhoneOrUsername);
        }

        User user = optionalUser.orElseThrow(() -> new AuthenticationException("Username/Email/Phone number not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if ("inactive".equals(user.getStatus())) {
            throw new AuthenticationException("Account has been locked");
        }

        return JwtUtil.generateToken(user);
    }

    /**
     * Add new user with default password (Admin function).
     *
     * @param user the user to add
     * @return the created user
     * @throws DuplicateResourceException if email or username already exists
     */
    public User addUser(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already in use");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already in use");
        }
        
        user.setPassword(passwordEncoder.encode("123456")); // Default password
        user.setStatus("active");
        return userRepository.save(user);
    }

    /**
     * Updates the authenticated user's information.
     *
     * @param updatedUser the user object with updated information
     * @return the updated user object
     * @throws RuntimeException if the user is not found or not authorized
     */
    public User updateUser(User updatedUser) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setUpdateAt(LocalDateTime.now());

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if(updatedUser.getAddress() != null){
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getEmail() != null){
            existingUser.setEmail(updatedUser.getEmail());
        }
        if(updatedUser.getUsername() != null){
            existingUser.setUsername(updatedUser.getUsername());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Logs out a user by invalidating their current JWT token.
     *
     * @param authHeader the Authorization header (must contain Bearer token)
     */
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Jwt jwt = jwtDecoder.decode(token);

        String jti = jwt.getId();
        Instant exp = jwt.getExpiresAt();

        if (!invalidatedTokenRepo.existsByJti(jti)) {
            InvalidatedToken invalidatedToken = new InvalidatedToken(jti, exp);
            invalidatedTokenRepo.save(invalidatedToken);
        }
    }
    /**
     * Get user by ID.
     *
     * @param id the user ID
     * @return optional containing user if found
     */
    public Optional<User> getUserById(Integer id){
        return userRepository.findById(id);
    }

    /**
     * Get all users.
     *
     * @return list of all users
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Find user by username.
     *
     * @param username the username to search for
     * @return the user with specified username
     * @throws ResourceNotFoundException if user is not found
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Changes the authenticated user's password after validating the old password.
     *
     * @param oldPassword the current password for verification
     * @param newPassword the new password to set
     * @param confirmNewPassword confirmation of the new password
     * @throws RuntimeException if old password is incorrect, passwords don't match, or new password is too short
     */
    public void changePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new RuntimeException("New passwords do not match");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Generates a random 5-character code with letters and numbers.
     */
    /**
     * Generate random 5-character reset code.
     *
     * @return the generated reset code
     */
    private String generateResetCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    /**
     * Initiates password reset process by sending reset code via email.
     *
     * @param email the email address to send reset code to
     * @throws RuntimeException if email is not found
     */
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String resetCode = generateResetCode();
        Instant expiryTime = Instant.now().plusSeconds(900); // 15 minutes

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(resetCode);
        token.setEmail(email);
        token.setExpiryTime(expiryTime);
        passwordResetTokenRepository.save(token);

        emailService.sendResetPasswordEmail(email, resetCode);
    }

    /**
     * Validates reset code.
     *
     * @param code the 5-character reset code to validate
     * @throws RuntimeException if code is invalid or expired
     */
    public void validateResetCode(String code) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(code)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset code"));

        if (resetToken.getExpiryTime().isBefore(Instant.now())) {
            throw new RuntimeException("Reset code has expired");
        }
    }

    /**
     * Resets user password using reset code.
     *
     * @param code the 5-character reset code
     * @param newPassword the new password
     * @param confirmPassword confirmation of new password
     * @throws RuntimeException if code is invalid, expired, or passwords don't match
     */
    public void resetPassword(String code, String newPassword, String confirmPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(code)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset code"));

        if (resetToken.getExpiryTime().isBefore(Instant.now())) {
            throw new RuntimeException("Reset code has expired");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // Admin methods
    /**
     * Create new user (Admin function).
     *
     * @param user the user to create
     * @return the created user
     * @throws DuplicateResourceException if email or username already exists
     */
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already in use");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already in use");
        }
        
        user.setPassword(passwordEncoder.encode("123456")); // Default password
        user.setStatus("active");
        return userRepository.save(user);
    }

    /**
     * Update user's role (Admin function).
     *
     * @param userId the user ID
     * @param roleId the new role ID
     * @return the updated user
     * @throws ResourceNotFoundException if user or role is not found
     * @throws IllegalArgumentException if trying to change admin role
     */
    public User updateUserRole(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        

        if ("ADMIN".equals(user.getRole().getRole())) {
            throw new IllegalArgumentException("Cannot change admin role");
        }
        
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        
        user.setRole(role);
        user.setUpdateAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Toggle user status between active and inactive (Admin function).
     *
     * @param userId the user ID
     * @return the updated user
     * @throws ResourceNotFoundException if user is not found
     * @throws IllegalArgumentException if trying to lock admin account
     */
    public User toggleUserStatus(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        

        if ("ADMIN".equals(user.getRole().getRole())) {
            throw new IllegalArgumentException("Cannot lock admin account");
        }
        
        String newStatus = "active".equals(user.getStatus()) ? "inactive" : "active";
        user.setStatus(newStatus);
        user.setUpdateAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Delete user (Admin function).
     *
     * @param userId the user ID to delete
     * @throws ResourceNotFoundException if user is not found
     * @throws IllegalArgumentException if trying to delete admin account
     */
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        

        if ("ADMIN".equals(user.getRole().getRole())) {
            throw new IllegalArgumentException("Cannot delete admin account");
        }
        
        userRepository.deleteById(userId);
    }
}
