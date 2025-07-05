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

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(new User()));


        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(testUser);
        });

        // Assert
        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository,times(1)).findByEmail(testUser.getEmail());
        verify(userRepository, never()).findByPhone(anyString());
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
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
    void testUpdateUser_Success() {
        // Arrange
        String currentUserEmail = "test@gmail.com";

        User updatedInfo = User.builder()
                .name("New Test User")
                .phone("999888777")
                .address(null)
                .email(null)
                .username(null)
                .build();

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(currentUserEmail);

            when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(testUser));


            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User resultUser = userService.updateUser(updatedInfo);

            // Assert
            assertNotNull(resultUser);
            assertEquals("New Test User", resultUser.getName());
            assertEquals("999888777", resultUser.getPhone());
            assertEquals("123 somewhere street", resultUser.getAddress());
            assertEquals("test@gmail.com", resultUser.getEmail());

            verify(userRepository, times(1)).findByEmail(currentUserEmail);
            verify(userRepository, times(1)).save(testUser);
        }
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
}
