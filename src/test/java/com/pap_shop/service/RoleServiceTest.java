package com.pap_shop.service;

import com.pap_shop.entity.Roles;
import com.pap_shop.entity.User;
import com.pap_shop.repository.RoleRepository;
import com.pap_shop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void updateRoleUserByID_success(){
        // Arrange
        int userId = 1;
        int roleId = 2;

        Roles role = new Roles();
        role.setRoleId(roleId);
        role.setRole("USER");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleId(roleId)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = roleService.updateRoleUserByID(userId,roleId);

        // Assert
        assertNotNull(result);
        assertEquals(role,result.getRole());
        verify(userRepository).findById(userId);
        verify(roleRepository).findByRoleId(roleId);
        verify(userRepository).save(user);
    }

    @Test
    void updateRoleUserByID_UserNotFound(){
        int userId = 1;
        int roleId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> roleService.updateRoleUserByID(userId, roleId));

        assertEquals("User not found",exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(roleRepository);
    }

    @Test
    void updateRoleUserByID_RoleNotFound(){
        int userId = 1;
        int roleId = 1;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleId(roleId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> roleService.updateRoleUserByID(userId,roleId));

        assertEquals("Role not exist",exception.getMessage());
        verify(userRepository).findById(userId);
        verify(roleRepository).findByRoleId(roleId);
    }
}
