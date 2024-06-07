package com.spring_restful.sandbox.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest webRequest, WebDataBinderFactory factory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");
        System.out.println(token);

        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userRepository.findFirstByToken(token).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized 2")
        );

        if(user.getTokenExpiredAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized 3");
        }

        return user;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }


    
}
