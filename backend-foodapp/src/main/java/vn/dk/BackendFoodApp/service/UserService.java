package vn.dk.BackendFoodApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dk.BackendFoodApp.model.Role;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.UserRepository;
import vn.dk.BackendFoodApp.utils.PasswordUtils;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    public User handleGetUserByUserName(String username){
        return userRepository.findByUsername(username);
    }

    public void updateUserToken(String token, String username){
        User currentUser = this.handleGetUserByUserName(username);
        if(currentUser != null){
            currentUser.setRefreshToken(token);
            userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndUsername(String token, String username){
        return userRepository.findByRefreshTokenAndUsername(token,username);
    }

    public User getUserByToken(){
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();
        if (usernameOpt.get() == null)
        {
            return null;
        }
        User user = handleGetUserByUserName(usernameOpt.get());
        return user;
    }

    public boolean isAccountValid(String email)
    {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        // If user exist, then check if they are activated
        if (user.isPresent())
        {
            boolean isAccountActivated = user.get().isActive();
            return  !isAccountActivated;
        }
        return true;
    }
    public void activateAccount(String email)
    {
        User user = userRepository.findByEmail(email);
        if (user != null)
        {
            user.setActive(true);
            userRepository.save(user);
        }
    }
    public User createNewUser(String userName, String password, String email)
    {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User existedUser = userRepository.findByEmail(email);
        if (existedUser!= null) {
            existedUser.setPassword(encodedPassword);
            existedUser.setUsername(userName);
            existedUser.setActive(false);
            userRepository.save(existedUser);
            return null;
        }
        else {
            Role defaultRole = roleService.getRoleByName(Role.USER);
            User user = new User();
            user.setEmail(email);
            user.setUsername(userName);
            user.setPassword(encodedPassword);
            user.setRole(defaultRole);
            user.setActive(false);
            userRepository.save(user);
            return user;
        }
    }
    public boolean isUsernameExisted(String username)
    {
        User user = userRepository.findByUsername(username);
        if (user != null)
        {
            return true;
        }
        return false;
    }
    public boolean isPasswordValid(String password)
    {
        return PasswordUtils.isPasswordValid(password);
    }

    public String getRefreshToken(String username)
    {
        return userRepository.findOptionalRefreshTokenByUsername(username).get();
    }

    public void saveRefreshToken(String refreshToken) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        user.setRefreshToken(refreshToken); // gán refresh token mới
        userRepository.save(user); // cập nhật lại trong DB
    }

    public void updateUserProfile(User user)
    {
        userRepository.save(user);
    }
}
