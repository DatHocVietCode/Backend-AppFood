package vn.dk.BackendFoodApp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.response.user.UserResponse;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;

import java.util.List;
import java.util.Optional;


@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/my-profile")
    @Secured("ROLE_USER")
    public ResponseEntity<ResponseObject> getMyProfile() {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ResponseObject.builder().status(HttpStatus.UNAUTHORIZED.value()).data(null).message("UnAuthorized").build());
        }

        String username = usernameOpt.get();
        User user = userService.handleGetUserByUserName(username);

        if (user == null) {
            return ResponseEntity.status(404).body(ResponseObject.builder().status(404).data(null).message("Not found").build());
        }

        UserResponse userResponse = UserResponse.fromUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder().message("My profile success").data(userResponse).status(HttpStatus.OK.value()).build());
    }
}
