package vn.dk.BackendFoodApp.dto.response.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpResponse {
    private String email; // Email đã đăng ký để client xác nhận
    private String username; // Tên người dùng đã đăng ký (nếu có)
}
