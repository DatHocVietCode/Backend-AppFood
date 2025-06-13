package vn.dk.BackendFoodApp.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {

    @NotBlank(message = "username khong duoc de trong")
    private String username;

    @NotBlank(message = "password khong duoc de trong")
    private String password;
}
