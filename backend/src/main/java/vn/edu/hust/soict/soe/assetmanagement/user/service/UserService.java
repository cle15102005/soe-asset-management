package vn.edu.hust.soict.soe.assetmanagement.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hust.soict.soe.assetmanagement.exception.BusinessRuleException;
import vn.edu.hust.soict.soe.assetmanagement.exception.ResourceNotFoundException;
import vn.edu.hust.soict.soe.assetmanagement.user.dto.CreateUserRequest;
import vn.edu.hust.soict.soe.assetmanagement.user.dto.UserDto;
import vn.edu.hust.soict.soe.assetmanagement.user.entity.Role;
import vn.edu.hust.soict.soe.assetmanagement.user.entity.User;
import vn.edu.hust.soict.soe.assetmanagement.user.repository.RoleRepository;
import vn.edu.hust.soict.soe.assetmanagement.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * User service.
 * Also implements UserDetailsService so Spring Security
 * can load users by username during authentication.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ── UserDetailsService (required by Spring Security) ──────

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng: " + username));
    }

    // ── CRUD ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với id: " + id));
        return UserDto.from(user);
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleException(
                    "Tên đăng nhập đã tồn tại: " + request.getUsername());
        }
        if (request.getEmail() != null
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException(
                    "Email đã được sử dụng: " + request.getEmail());
        }

        Role role = roleRepository.findByCode(request.getRoleCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy vai trò: " + request.getRoleCode()));

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .isActive(true)
                .build();

        user.getRoles().add(role);
        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }
}