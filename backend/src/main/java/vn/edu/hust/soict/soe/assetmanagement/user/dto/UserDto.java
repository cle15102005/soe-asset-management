package vn.edu.hust.soict.soe.assetmanagement.user.dto;

import lombok.Builder;
import lombok.Getter;
import vn.edu.hust.soict.soe.assetmanagement.user.entity.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User DTO — safe to return from API (no password hash).
 */
@Getter
@Builder
public class UserDto {

    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private Set<String> roles;
    private Set<String> managingUnitCodes;

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .roles(user.getRoles().stream()
                        .map(r -> r.getCode())
                        .collect(Collectors.toSet()))
                .managingUnitCodes(user.getManagingUnits().stream()
                        .map(u -> u.getCode())
                        .collect(Collectors.toSet()))
                .build();
    }
}