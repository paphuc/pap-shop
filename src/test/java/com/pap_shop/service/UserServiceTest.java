package com.pap_shop.service;

import com.pap_shop.entity.User;
import com.pap_shop.repository.UserRepository;
import io.jsonwebtoken.lang.Assert;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private  UserService userService;

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
