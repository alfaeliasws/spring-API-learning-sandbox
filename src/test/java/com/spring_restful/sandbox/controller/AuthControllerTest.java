package com.spring_restful.sandbox.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.LoginUserRequest;
import com.spring_restful.sandbox.model.TokenResponse;
import com.spring_restful.sandbox.model.WebResponse;
import com.spring_restful.sandbox.repository.UserRepository;
import com.spring_restful.sandbox.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginFailedUserNotFound()throws Exception { 
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");


        mockMvc.perform(
            post("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }
    
    @Test
    void loginFailedWrongPassword()throws Exception { 
        User user = new User();
        user.setName("test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("salah");

        mockMvc.perform(
            post("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginSuccess()throws Exception { 
        User user = new User();
        user.setName("test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
            post("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo(result->{
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertEquals(userDb.getToken(), response.getData().getToken());
            assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());

        });
    }

    @Test
    void logoutFailed () throws Exception{
        mockMvc.perform(
            delete("/api/auth/logout")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void logoutSuccess () throws Exception{
        User user = new User();
        user.setName("test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        mockMvc.perform(
            delete("/api/auth/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertNull(userDb.getTokenExpiredAt());
            assertNull(userDb.getToken());
        });
    }
}
