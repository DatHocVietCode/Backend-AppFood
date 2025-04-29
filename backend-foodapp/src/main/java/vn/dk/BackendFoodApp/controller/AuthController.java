package vn.dk.BackendFoodApp.controller;


import com.nimbusds.jose.proc.SecurityContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.user.UserLoginDTO;
import vn.dk.BackendFoodApp.service.TokenService;

import java.security.Security;

@RestController
public class AuthController {

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseObject login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //crate a token
        String access_token = tokenService.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseObject.builder().message("Login success").data(access_token).status(HttpStatus.OK.value()).build();
    }
}
