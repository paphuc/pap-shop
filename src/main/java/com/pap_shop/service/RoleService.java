package com.pap_shop.service;

import org.springframework.stereotype.Service;
import com.pap_shop.entity.Roles;
import com.pap_shop.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Roles addRole(Roles role){
        return roleRepository.save(role);
    }

    public List<Roles> getAllRoles(){
        return roleRepository.findAll();
    }
}

