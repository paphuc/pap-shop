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

        User user = optionalUser.orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        return JwtUtil.generateToken(user);
    }

    public User addUser(User user){
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
    public Optional<User> getUserById(Integer id){
        return userRepository.findById(id);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
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
     * Initiates password reset process by sending reset email.
     *
     * @param email the email address to send reset link to
     * @throws RuntimeException if email is not found
     */
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String resetToken = UUID.randomUUID().toString();
        Instant expiryTime = Instant.now().plusSeconds(900); // 15 minutes

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(resetToken);
        token.setEmail(email);
        token.setExpiryTime(expiryTime);
        passwordResetTokenRepository.save(token);

        emailService.sendResetPasswordEmail(email, resetToken);
    }

    /**
     * Validates reset token.
     *
     * @param token the reset token to validate
     * @throws RuntimeException if token is invalid or expired
     */
    public void validateResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.getExpiryTime().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token has expired");
        }
    }

    /**
     * Resets user password using reset token.
     *
     * @param token the reset token
     * @param newPassword the new password
     * @param confirmPassword confirmation of new password
     * @throws RuntimeException if token is invalid, expired, or passwords don't match
     */
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.getExpiryTime().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token has expired");
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
}
