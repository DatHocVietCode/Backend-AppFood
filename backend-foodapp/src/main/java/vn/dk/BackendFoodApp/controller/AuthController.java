package vn.dk.BackendFoodApp.controller;


import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.auth.OTPReceivedDTO;
import vn.dk.BackendFoodApp.dto.request.auth.UserLoginDTO;
import vn.dk.BackendFoodApp.dto.request.auth.UserSignUpDTO;
import vn.dk.BackendFoodApp.dto.response.auth.AccessTokenResponse;
import vn.dk.BackendFoodApp.dto.response.auth.LoginResponse;
import vn.dk.BackendFoodApp.dto.response.auth.SignUpResponse;
import vn.dk.BackendFoodApp.exception.InvalidDataException;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.service.CartService;
import vn.dk.BackendFoodApp.service.OTPService;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;
import vn.dk.BackendFoodApp.utils.EmailValidatorUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Autowired
    OTPService otpService;

    @Autowired
    CartService cartService;

    @PostMapping("/signUp")
    public ResponseEntity<ResponseObject> signUp(@Valid @RequestBody UserSignUpDTO userSignUpDTO) throws MessagingException, UnsupportedEncodingException {
        SignUpResponse response = new SignUpResponse();
        if (!EmailValidatorUtils.isValidEmail(userSignUpDTO.getEmail()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CONFLICT.value())
                            .message("Email is invalid")
                            .build()
            );
        }
        if (!userService.isAccountValid(userSignUpDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CONFLICT.value())
                            .message("Email already exists or account is activated!")
                            .build()
            );
        }

        if (userService.isUsernameExisted(userSignUpDTO.getUserName()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CONFLICT.value())
                            .message("Username is already existed!")
                            .build()
            );
        }

        if (!userService.isPasswordValid(userSignUpDTO.getPassword()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CONFLICT.value())
                            .message("Password is not strong!")
                            .build()
            );
        }
        User user = userService.createNewUser(userSignUpDTO.getUserName(), userSignUpDTO.getPassword(), userSignUpDTO.getEmail());
        if (user != null)
        {
            cartService.createCartForUser(user);
        }
        String otpToken = otpService.sendOTP(userSignUpDTO.getEmail());
        response.setEmail(userSignUpDTO.getEmail());
        response.setUsername(userSignUpDTO.getUserName());
        response.setOtpToken(otpToken);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Sign up successfully! Verify your account by the otp code we have sent to your email!")
                .data(response)
                .build());
    }


    @PostMapping("/verifyOTP")
    public ResponseEntity<ResponseObject> receivedOTP(@Valid @RequestBody OTPReceivedDTO receivedOTPDTO)
    {
        System.out.println(receivedOTPDTO.getEmail());
        System.out.println(receivedOTPDTO.getOtpToken());
        System.out.println(receivedOTPDTO.getOtpCode());
        boolean isValid = otpService.ValidateReceivedOTP(receivedOTPDTO);
        System.out.println(isValid);
        if (isValid)
        {

            userService.activateAccount(receivedOTPDTO.getEmail());
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK.value())
                    .message("Successfully activated your account!")
                    .build());

        }
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Activated failed!")
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());

        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    
        //set thông tin người dùng đăng nhập vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse res = new LoginResponse();
        User user = userService.handleGetUserByUserName(userLoginDTO.getUsername());
        if (user != null){
            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(user.getId(),user.getUsername(), user.getRole().getName(), user.getFullName());
            res.setUser(userLogin);
        }
        //crate a access token
        String access_token = tokenService.createAccessToken(authentication.getName(), res);
        boolean isRefreshTokenValid = tokenService.isRefreshTokenValid(authentication.getName());
        if (isRefreshTokenValid)
        {
            res.setRefreshToken(userService.getRefreshToken(authentication.getName()));

        }
        else
        {
            String refreshToken = tokenService.createRefreshToken(authentication.getName(), res);
            res.setRefreshToken(refreshToken);
            userService.saveRefreshToken(refreshToken);
            System.out.println("Refresh token length: " + refreshToken.length());
        }
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

    @PostMapping("/refreshAccessToken")
    public ResponseEntity<ResponseObject> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        String username = tokenService.getUsernameFromRefreshToken(refreshToken);
        System.out.println("Refresh Token is called: " + refreshToken);
        if (username == null)
        {
            System.out.println("Token invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message("Can not get username")
                            .data(null).build());

        }
        // Kiểm tra token có hợp lệ không
        if (!tokenService.isRefreshTokenValid(username)) {
            System.out.println("Token invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message("Invalid refreshToken!")
                            .data(null).build());

        }

        LoginResponse res = new LoginResponse();
        User user = userService.handleGetUserByUserName(username);
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(user.getId(), user.getUsername(), user.getRole().getName(), user.getFullName());
        res.setUser(userLogin);
        // Tạo access token mới
        String newAccessToken = tokenService.createAccessToken(username, res);
        System.out.println("New accessToken from refresh endpoint: " + newAccessToken);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccessToken(newAccessToken);

        List<AccessTokenResponse> data = new ArrayList<>();
        data.add(accessTokenResponse);

        return ResponseEntity.status(HttpStatus.OK.value())
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Refresh successfully!")
                        .data(data)
                        .build());
    }
}
