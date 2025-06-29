package com.pap_shop.service;

import com.pap_shop.entity.Roles;
import com.pap_shop.entity.User;
import com.pap_shop.repository.RoleRepository;
import com.pap_shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private  UserService userService;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = User.builder()
                .id(1)
                .username("testUser")
                .password("123456")
                .email("test@gmail.com")
                .phone("123456789")
                .name("Test User")
                .address("123 somewhere street")
                .build();
    }

    @Test
    void testRegister_Success(){
        //Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

        Roles userRole = new Roles();
        userRole.setID_role(2);
        userRole.setRole("User");
        when(roleRepository.findById(2)).thenReturn(Optional.of(userRole));

        //Act
        userService.register(testUser);

        //Assert
        verify(passwordEncoder, times(1)).encode("123456");
        verify(roleRepository, times(1)).findById(2);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("encodedPassword", testUser.getPassword());
        assertEquals(userRole, testUser.getRole());
    }

    @Test
    void testRegister_FailsWhenEmailInUse() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(testUser);
        });
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_FailsWhenPasswordIsTooShort() {
        // Arrange
        testUser.setPassword("123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(testUser);
        });
        assertEquals("Password must be at least 6 characters", exception.getMessage());
    }

}
