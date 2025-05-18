package com.pap_shop.repository;
import com.pap_shop.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoleRepository extends JpaRepository<Roles, Integer> {
}
