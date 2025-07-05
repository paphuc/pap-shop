package com.pap_shop.controller;


import com.pap_shop.dto.UpdateRoleRequest;
import com.pap_shop.entity.Roles;
import com.pap_shop.service.RoleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<Roles> getAllRoles(){
        return roleService.getAllRoles();
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRoleById(@RequestBody UpdateRoleRequest request) {
        roleService.updateRoleUserByID(request.getUserId(), request.getRoleId());
        return ResponseEntity.ok("Success!");
    }
}
