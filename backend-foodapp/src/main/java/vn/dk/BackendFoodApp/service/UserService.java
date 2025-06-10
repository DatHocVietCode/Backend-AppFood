package vn.dk.BackendFoodApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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

    public boolean isUserEmailExisted(String email)
    {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        return user.isPresent();
    }
}
