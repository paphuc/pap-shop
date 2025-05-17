package com.pap_shop.controller;


import com.pap_shop.entity.Roles;
import com.pap_shop.service.RoleService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.Role;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping
    public Roles addRole(@RequestBody Roles roles){
        return roleService.addRole(roles);
    }
}
