package vn.dk.BackendFoodApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User handleGetUserByUserName(String username){
        return userRepository.findByEmail(username);
    }
}
