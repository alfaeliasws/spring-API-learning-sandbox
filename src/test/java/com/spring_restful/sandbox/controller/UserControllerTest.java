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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.RegisterUserRequest;
import com.spring_restful.sandbox.model.TokenResponse;
import com.spring_restful.sandbox.model.UpdateUserRequest;
import com.spring_restful.sandbox.model.UserResponse;
import com.spring_restful.sandbox.model.WebResponse;
import com.spring_restful.sandbox.repository.UserRepository;
import com.spring_restful.sandbox.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }


    @Test
    void testRegisterSuccess() throws Exception{
        
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("johnson");
        request.setPassword("rahasia");
        request.setName("test");

        mockMvc.perform(
            post("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

                assertEquals("OK", response.getData());
            }
        );
    }

    @Test
    void testRegisterBadRequest() throws Exception{
        
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
            post("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

                assertNotNull(response.getErrors());
            }
        );
    }

    @Test
    void testRegisterDuplicate() throws Exception{
        User user = new User();
        user.setUsername("johnson");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        userRepository.save(user);


        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("johnson");
        request.setPassword("rahasia");
        request.setName("test");

        mockMvc.perform(
            post("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

                assertNotNull(response.getErrors());
            }
        );
    }

    @Test
    void getUserUnauthorized() throws Exception{

        mockMvc.perform(
            get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenNotSent() throws Exception{
        mockMvc.perform(
            get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception{
        User user = new User();
        user.setUsername("mike");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);

        userRepository.save(user);

        mockMvc.perform(
            get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
            assertEquals("mike", response.getData().getUsername());
            assertEquals("test", response.getData().getName());

        });
    }


    @Test
    void getUserTokenExpired() throws Exception{
        User user = new User();
        user.setUsername("mike");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 1000000000L);

        userRepository.save(user);

        mockMvc.perform(
            get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserUnauthorized() throws Exception{
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
            patch("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserSuccess() throws Exception{
        User user = new User();
        user.setUsername("mike");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("bilijin");
        request.setPassword("rahasiaBanget");

        mockMvc.perform(
            patch("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
            assertEquals("bilijin", response.getData().getName());
            assertEquals("mike", response.getData().getUsername());


            User userDB = userRepository.findById("mike").orElse(null);

            assertNotNull(userDB);
            assertTrue(BCrypt.checkpw("rahasiaBanget", userDB.getPassword()));

        });
    }

}
