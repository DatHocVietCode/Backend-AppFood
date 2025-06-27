package vn.dk.BackendFoodApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.response.auth.UserResponse;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;


@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Value("${foodapp.upload-file.base-path}")
    private String uploadDir;


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
    @PostMapping("/update-profile")
    @Secured("ROLE_USER") // Chỉ người đã đăng nhập mới được cập nhật
    public ResponseEntity<ResponseObject> updateProfile(
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("phone") String phone,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseObject.builder().status(401).message("Unauthorized").data(null).build());
        }

        String username = usernameOpt.get();
        User user = userService.handleGetUserByUserName(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder().status(404).message("User not found").data(null).build());
        }

        // Cập nhật thông tin cơ bản
        user.setFullName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Tạo tên file mới (random)
                String filename = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();

                // Bỏ prefix "file:/" để lấy đường dẫn vật lý thực tế
                String rawPath = uploadDir.replace("file:///", "").replace("file:\\", "");

                // Tạo thư mục nếu chưa tồn tại
                File dir = new File(rawPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Lưu file vào ổ đĩa
                Path uploadPath = Paths.get(rawPath, filename);
                avatarFile.transferTo(uploadPath.toFile());

                // Cập nhật tên file vào DB (client sẽ sử dụng IMG_URL + filename để hiển thị)
                user.setProfileImage(filename);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ResponseObject.builder()
                                .status(500)
                                .message("Failed to upload image")
                                .data(null)
                                .build());
            }
        }


        // Lưu vào DB
        userService.updateUserProfile(user);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(200)
                .message("Cập nhật thông tin thành công")
                .data(UserResponse.fromUser(user))
                .build());
    }

}
