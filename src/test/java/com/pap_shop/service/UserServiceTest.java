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
}
