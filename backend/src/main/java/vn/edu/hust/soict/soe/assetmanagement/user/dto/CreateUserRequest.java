package vn.edu.hust.soict.soe.assetmanagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /api/users
 */
@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống.")
    @Size(min = 3, max = 100,
          message = "Tên đăng nhập phải từ 3 đến 100 ký tự.")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự.")
    private String password;

    @NotBlank(message = "Họ và tên không được để trống.")
    private String fullName;

    @Email(message = "Email không hợp lệ.")
    private String email;

    private String phone;

    @NotBlank(message = "Vai trò không được để trống.")
    private String roleCode;  // e.g. SYSTEM_ADMIN, ASSET_MANAGER, WAREHOUSE...
}