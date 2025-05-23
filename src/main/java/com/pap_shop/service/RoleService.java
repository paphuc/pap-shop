package com.pap_shop.service;

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

    public Roles addRole(Roles role){
        return roleRepository.save(role);
    }

    public List<Roles> getAllRoles(){
        return roleRepository.findAll();
    }
}

