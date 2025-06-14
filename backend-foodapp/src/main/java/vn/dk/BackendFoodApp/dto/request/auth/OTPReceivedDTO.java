package vn.dk.BackendFoodApp.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPReceivedDTO {
    private String email;
    private String otpCode;
    private String otpToken;
}
