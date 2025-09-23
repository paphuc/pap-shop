package com.pap_shop.service;

import com.pap_shop.entity.Roles;
import com.pap_shop.entity.User;
import com.pap_shop.repository.RoleRepository;
import com.pap_shop.repository.UserRepository;
import com.pap_shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        userRole.setRoleId(2);
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
    void testLogin_SuccessWithEmail() {
        // Arrange
        String loginIdentifier = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        String expectedToken = "mocked-jwt-token";


        testUser.setPassword(encodedPassword);

        when(userRepository.findByEmail(loginIdentifier)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.generateToken(testUser)).thenReturn(expectedToken);

            // Act
            String actualToken = userService.login(loginIdentifier, rawPassword);

            // Assert
            assertEquals(expectedToken, actualToken);

            verify(userRepository).findByEmail(loginIdentifier);
            verify(passwordEncoder).matches(rawPassword, encodedPassword);
        }
    }


    @Test
    void testLogin_SuccessWithPhone() {
        // Arrange
        String loginIdentifier = "123456789";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        String expectedToken = "mocked-jwt-token";

        testUser.setPassword(encodedPassword);

        when(userRepository.findByPhone(loginIdentifier)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.generateToken(testUser)).thenReturn(expectedToken);

            // Act
            String actualToken = userService.login(loginIdentifier, rawPassword);

            // Assert
            assertEquals(expectedToken, actualToken);
            verify(userRepository).findByPhone(loginIdentifier);
        }
    }

    @Test
    void testLogin_SuccessWithUsername() {
        // Arrange
        String loginIdentifier = "testUser";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        String expectedToken = "mocked-jwt-token";

        testUser.setPassword(encodedPassword);

        when(userRepository.findByUsername(loginIdentifier)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.generateToken(testUser)).thenReturn(expectedToken);

            // Act
            String actualToken = userService.login(loginIdentifier, rawPassword);

            // Assert
            assertEquals(expectedToken, actualToken);
            verify(userRepository).findByUsername(loginIdentifier);
        }
    }

    @Test
    void testAddUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User savedUser = userService.addUser(testUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testFindUserById(){
        //Arrange
        User user = new User();
        user.setId(1);
        user.setName("hehe");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        //Act
        Optional<User> result =  userService.getUserById(1);

        //Assert
        assertEquals("hehe",result.get().getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetAllUsers(){
        //Arrange
        User user1 = new User();
        user1.setName("Pham Anh A");
        User user2 = new User();
        user2.setName("Nguyen Van B");

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        //Act
        List<User> result = userService.getAllUsers();

        //Assert
        assertEquals(2,result.size());
        assertEquals("Pham Anh A",result.get(0).getName());
        assertEquals("Nguyen Van B",result.get(1).getName());
        verify(userRepository,times(1)).findAll();
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        
        User result = userService.findByUsername("testUser");
        
        assertEquals(testUser, result);
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void findByUsername_whenUserNotExists_shouldThrowException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> userService.findByUsername("nonexistent"));
    }

    @Test
    void createUser_shouldCreateUserWithDefaultPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.createUser(testUser);
        
        assertNotNull(result);
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserRole_shouldUpdateRole() {
        Roles newRole = new Roles();
        newRole.setRoleId(3);
        newRole.setRole("MANAGER");
        
        Roles currentRole = new Roles();
        currentRole.setRole("USER");
        testUser.setRole(currentRole);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(3)).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.updateUserRole(1, 3);
        
        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void toggleUserStatus_shouldToggleStatus() {
        Roles userRole = new Roles();
        userRole.setRole("USER");
        testUser.setRole(userRole);
        testUser.setStatus("active");
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.toggleUserStatus(1);
        
        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        Roles userRole = new Roles();
        userRole.setRole("USER");
        testUser.setRole(userRole);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        
        userService.deleteUser(1);
        
        verify(userRepository).deleteById(1);
    }
}
