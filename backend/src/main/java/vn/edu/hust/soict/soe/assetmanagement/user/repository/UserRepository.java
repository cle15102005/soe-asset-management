package vn.edu.hust.soict.soe.assetmanagement.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hust.soict.soe.assetmanagement.user.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * User repository.
 * findByUsername() is the critical method — used by AuthService
 * and Spring Security's UserDetailsService.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}