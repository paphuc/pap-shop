package com.pap_shop.repository;
import com.pap_shop.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByRole(String role);
    Optional<Roles> findByRoleId(Integer roleId);
}
