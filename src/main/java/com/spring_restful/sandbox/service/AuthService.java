package com.spring_restful.sandbox.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.LoginUserRequest;
import com.spring_restful.sandbox.model.TokenResponse;
import com.spring_restful.sandbox.repository.UserRepository;
import com.spring_restful.sandbox.security.BCrypt;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password Wrong");
        }
        

    }
    
    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
