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
        int user_id = 1;
        int role_id = 2;

        Roles role = new Roles();
        role.setRoleId(role_id);
        role.setRole("USER");

        User user = new User();
        user.setId(user_id);

        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleId(role_id)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = roleService.updateRoleUserByID(user_id,role_id);

        // Assert
        assertNotNull(result);
        assertEquals(role,result.getRole());
        verify(userRepository).findById(user_id);
        verify(roleRepository).findByRoleId(role_id);
        verify(userRepository).save(user);
    }

    @Test
    void updateRoleUserByID_UserNotFound(){
        int user_id = 1;
        int role_id = 2;

        when(userRepository.findById(user_id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> roleService.updateRoleUserByID(user_id,role_id));

        assertEquals("User not found",exception.getMessage());
        verify(userRepository).findById(user_id);
        verifyNoInteractions(roleRepository);
    }

    @Test
    void updateRoleUserByID_RoleNotFound(){
        int user_id = 1;
        int role_id = 1;

        User user = new User();
        user.setId(user_id);

        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleId(role_id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> roleService.updateRoleUserByID(user_id,role_id));

        assertEquals("Role not exist",exception.getMessage());
        verify(userRepository).findById(user_id);
        verify(roleRepository).findByRoleId(role_id);
    }
}
