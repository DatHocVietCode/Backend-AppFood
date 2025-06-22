package vn.dk.BackendFoodApp.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import vn.dk.BackendFoodApp.model.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private UserLogin user;

    //user's detail
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin{
        private Long id;
        private String username;
        private String role;
        private String fullname;
    }
}
