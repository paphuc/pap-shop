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

    /**
     * Saves a new role to the database.
     *
     * @param role The role object to be created and saved. Must not be null.
     * @return The saved role entity, including any database-generated values (e.g., ID).
     */
    public Roles addRole(Roles role){
        return roleRepository.save(role);
    }

    /**
     * Retrieves a list of all roles from the database.
     *
     * @return A List of all Roles objects. Returns an empty list if no roles are found.
     */
    public List<Roles> getAllRoles(){
        return roleRepository.findAll();
    }

    /**
     * Updates the role for a specific user identified by their ID.
     * <p>
     * This method first finds the user and the new role by their respective IDs.
     * If either is not found, it throws a RuntimeException. Otherwise, it assigns
     * the new role to the user and persists the change.
     *
     * @param userId The ID of the user whose role is to be updated.
     * @param roleId The ID of the new role to assign to the user.
     * @return The updated User object, saved with the new role.
     * @throws RuntimeException if a user with the specified {@code userId} is not found,
     *                        or if a role with the specified {@code roleId} does not exist.
     */
    public User updateRoleUserByID(Integer userId, Integer roleId){
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Roles newRole = roleRepository.findByRoleId(roleId)
                .orElseThrow(()-> new RuntimeException("Role not exist"));
        existingUser.setRole(newRole);
        return userRepository.save(existingUser);
    }
}

