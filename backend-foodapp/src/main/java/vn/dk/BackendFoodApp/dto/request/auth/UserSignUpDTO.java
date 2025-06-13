package vn.dk.BackendFoodApp.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
