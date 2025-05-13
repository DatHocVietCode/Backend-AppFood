package vn.dk.BackendFoodApp.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.user.UserLoginDTO;
import vn.dk.BackendFoodApp.dto.response.user.LoginResponse;
import vn.dk.BackendFoodApp.exception.InvalidDataException;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${security.authentication.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

        @PostMapping("/login")
        public ResponseEntity<ResponseObject> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
            //Nạp input gồm username/password vào Security
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            //xác thực người dùng => cần viết hàm loadUserByUsername
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    
            //set thông tin người dùng đăng nhập vào context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginResponse res = new LoginResponse();
            User user = userService.handleGetUserByUserName(userLoginDTO.getUsername());
            if(user != null){
                LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(user.getId(),user.getUsername(), user.getRole().getName(), user.getFullName());
                res.setUser(userLogin);
            }
            //crate a access token
            String access_token = tokenService.createAccessToken(authentication.getName(), res);
            res.setAccessToken(access_token);
    
            //crate a refresh token
//            String refresh_token = tokenService.createRefreshToken(userLoginDTO.getUsername(), res);
    
//            //update refresh token
//            userService.updateUserToken(refresh_token, userLoginDTO.getUsername());

//            res.setRefreshToken(refresh_token);
//            //set cookie
//            ResponseCookie resCookies = ResponseCookie.from("refresh_token",refresh_token)
//                    .httpOnly(true)
//                    .secure(true)
//                    .path("/")
//                    .maxAge(refreshTokenExpiration)
//                    .build();
    
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Login success")
                    .data(res)
                    .status(HttpStatus.OK.value())
                    .build());
        }

    @GetMapping("/account")
    public ResponseEntity<ResponseObject> getAccount(){
        String username = TokenService.getCurrentUserLogin().isPresent() ? TokenService.getCurrentUserLogin().get() : "";

        User user = userService.handleGetUserByUserName(username);
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
        if(user != null){
            userLogin.setId(user.getId());
            userLogin.setUsername(user.getUsername());
            userLogin.setRole(user.getRole().getName());
        }
        return ResponseEntity.ok().body(ResponseObject.builder().status(HttpStatus.OK.value()).message("Fetch account successful").data(userLogin).build());
    }
//    @GetMapping("/refresh")
//    public ResponseEntity<ResponseObject> getRefreshToken(
//            @CookieValue(name = "refresh_token", defaultValue = "abc") String refreshToken) {
//            if(refreshToken.equals("abc")){
//                throw new InvalidDataException("No refresh token at cookie");
//            }
//
//            //check valid
//            Jwt decodeToken = tokenService.checkValidRefreshToken(refreshToken);
//            String username = decodeToken.getSubject();
//
//            //check user by token + username
//            User currentUser = userService.getUserByRefreshTokenAndUsername(refreshToken, username);
//            if(currentUser == null){
//                throw new InvalidDataException("Refresh Token invalid");
//            }
//
//            //issue new token/set refresh token as cookies
//        LoginResponse res = new LoginResponse();
//        User user = userService.handleGetUserByUserName(username);
//        if(user != null){
//            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(user.getId(),user.getUsername(), user.getRole().getName());
//            res.setUser(userLogin);
//        }
//        //crate a access token
//        String access_token = tokenService.createAccessToken(username, res);
//        res.setAccessToken(access_token);
//
//        //crate a refresh token
//        String new_refresh_token = tokenService.createRefreshToken(username, res);
//
//        //update refresh token
//        userService.updateUserToken(new_refresh_token, username);
//
////        res.setRefreshToken(refreshToken);
////        //set cookie
////        ResponseCookie resCookies = ResponseCookie.from("refresh_token",new_refresh_token)
////                .httpOnly(true)
////                .secure(true)
////                .path("/")
////                .maxAge(refreshTokenExpiration)
////                .build();
//
//        return ResponseEntity.ok().body(ResponseObject.builder()
//                .message("Refresh Token success")
//                .data(res)
//                .status(HttpStatus.OK.value())
//                .build());
//    }
    @PostMapping("/logout")
    public ResponseEntity<ResponseObject> logout() throws InvalidDataException {
        String username = tokenService.getCurrentUserLogin().isPresent() ? tokenService.getCurrentUserLogin().get()
                : "";
        if (username.equals("")) {
            throw new InvalidDataException("Acces Token không hợp lệ");
        }

        // update refresh token == null
//        userService.updateUserToken(null, username);

//        ResponseCookie deleteSpringCookie = ResponseCookie
//                .from("refreshToken", null)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(0)
//                .build();
        return ResponseEntity.ok()
                .body(ResponseObject.builder().status(HttpStatus.OK.value()).message("Logout success").data(null).build());
    }
}
