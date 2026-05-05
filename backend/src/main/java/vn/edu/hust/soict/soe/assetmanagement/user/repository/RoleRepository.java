package vn.edu.hust.soict.soe.assetmanagement.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hust.soict.soe.assetmanagement.user.entity.Role;

import java.util.Optional;

/**
 * Role repository.
 * findByCode() is used by UserService when assigning a role to a new user.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByCode(String code);
}