package com.pap_shop.controller;


import com.pap_shop.dto.UpdateRoleRequest;
import com.pap_shop.entity.Roles;
import com.pap_shop.service.RoleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user roles.
 * Provides endpoints for role operations including creation, retrieval, and updates.
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;
    /**
     * Constructor to inject RoleService.
     *
     * @param roleService the service used for role operations
     */
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    /**
     * Add a new role.
     *
     * @param roles the role to be added
     * @return the added role
     */
    @PostMapping
    public Roles addRole(@RequestBody Roles roles){
        return roleService.addRole(roles);
    }

    /**
     * Get all roles.
     *
     * @return list of all roles
     */
    @GetMapping
    public List<Roles> getAllRoles(){
        return roleService.getAllRoles();
    }

    /**
     * Update user's role.
     *
     * @param request the update request containing user ID and new role ID
     * @return success message
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateRoleById(@RequestBody UpdateRoleRequest request) {
        roleService.updateRoleUserByID(request.getUserId(), request.getRoleId());
        return ResponseEntity.ok("Success!");
    }
}
