package com.pap_shop.service;

import com.pap_shop.entity.User;
import com.pap_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import com.pap_shop.entity.Roles;
import com.pap_shop.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    UserRepository userRepository;

    public Roles addRole(Roles role){
        return roleRepository.save(role);
    }

    public List<Roles> getAllRoles(){
        return roleRepository.findAll();
    }

    public User updateRoleUserByID(Integer user_id, Integer role_id){
        User existingUser = userRepository.findById(user_id)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Roles newRole = roleRepository.findByRoleId(role_id)
                .orElseThrow(()-> new RuntimeException("Role not exist"));
        existingUser.setRole(newRole);
        return userRepository.save(existingUser);
    }
}

