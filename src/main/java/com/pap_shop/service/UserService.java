package com.pap_shop.service;

import com.pap_shop.entity.Customer;
import com.pap_shop.repository.UserRepository;
import com.pap_shop.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor to inject UserRepository and initialize BCryptPasswordEncoder.
     *
     * @param userRepository the repository used for customer operations
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     *Register a new customer by encoding their password before saving to database.
     *
     * @param customer the customer to be registered
     * @return the saved customer object
     */
    public Customer register(Customer customer){
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return userRepository.save(customer);
    }

    /**
     * Logs in a user by checking their email or phone and matching the password.
     * If successful, a JWT token is generated.
     *
     * @param emailOrPhone the email or phone number for login
     * @param rawPassword the raw password entered by the user
     * @return a JWT token if the login is successful
     * @throws RuntimeException if the user is not found or credentials are invalid
     */
    public String login(String emailOrPhone, String rawPassword){
        Optional<Customer> optionalCustomer;
        if (emailOrPhone.contains("@")){
            optionalCustomer = userRepository.findByEmail(emailOrPhone);
        }else {
            optionalCustomer = userRepository.findByPhone(emailOrPhone);
        }
        Customer customer = optionalCustomer.orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(rawPassword, customer.getPassword())) {
            return JwtUtil.generateToken(customer.getEmail());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
