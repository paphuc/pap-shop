package com.pap_shop.service;

import com.pap_shop.entity.Roles;
import com.pap_shop.entity.User;
import com.pap_shop.repository.RoleRepository;
import com.pap_shop.repository.UserRepository;
import com.pap_shop.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    BCryptPasswordEncoder passwordEncoder;


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
            throw new IllegalArgumentException("Email already in use");
        }

        if (user.getPhone() == null || !user.getPhone().matches("^\\d{9,15}$")) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone already in use");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
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

        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
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
        String email = authentication.getName();


        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


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


}
