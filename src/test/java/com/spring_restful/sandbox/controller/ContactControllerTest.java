package com.spring_restful.sandbox.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_restful.sandbox.entity.Contact;
import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.ContactResponse;
import com.spring_restful.sandbox.model.CreateContactRequest;
import com.spring_restful.sandbox.model.UpdateContactRequest;
import com.spring_restful.sandbox.model.WebResponse;
import com.spring_restful.sandbox.repository.ContactRepository;
import com.spring_restful.sandbox.repository.UserRepository;
import com.spring_restful.sandbox.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    void setUp(){
        contactRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setUsername("mike");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        // contactRepository.deleteAll();
    }

    @Test
    void createContactBadRequest() throws JsonProcessingException, Exception{
        CreateContactRequest request = new CreateContactRequest();

        request.setFirstName("");
        request.setEmail(null);

        mockMvc.perform(
            post("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createContactSuccess() throws JsonProcessingException, Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Johntakpor");
        request.setLastName("Songkali");
        request.setEmail("songkali@g.com");
        request.setPhone("088999888777");


        mockMvc.perform(
            post("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});

            assertNull(response.getErrors());
            assertEquals("Johntakpor", response.getData().getFirstName());
            assertEquals("Songkali", response.getData().getLastName());
            assertEquals("songkali@g.com", response.getData().getEmail());
            assertEquals("088999888777", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void getContactNotFound() throws JsonProcessingException, Exception{
        mockMvc.perform(
            get("/api/contacts/21312341")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }
    
    @Test
    void getContactSuccess() throws JsonProcessingException, Exception{
        User user = userRepository.findById("mike").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Johnson");
        contact.setLastName("Takpor");
        contact.setEmail("john@g.com");
        contact.setPhone("088111222333");
        contactRepository.save(contact);
        
        mockMvc.perform(
            get("/api/contacts/" + contact.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});

            assertNull(response.getErrors());
            assertEquals("Johnson", response.getData().getFirstName());
            assertEquals("Takpor", response.getData().getLastName());
            assertEquals("john@g.com", response.getData().getEmail());
            assertEquals("088111222333", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void updateContactBadRequest() throws JsonProcessingException, Exception{
        UpdateContactRequest request = new UpdateContactRequest();

        request.setFirstName("");
        request.setEmail(null);

        mockMvc.perform(
            put("/api/contacts/1345")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateContactSuccess() throws JsonProcessingException, Exception{
        User user = userRepository.findById("mike").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Johnson");
        contact.setLastName("Takpor");
        contact.setEmail("john@g.com");
        contact.setPhone("088111222333");
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Johntakpor");
        request.setLastName("Songkali");
        request.setEmail("songkali@g.com");
        request.setPhone("088999888777");


        mockMvc.perform(
            put("/api/contacts/" + contact.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>(){});

            assertNull(response.getErrors());
            assertEquals("Johntakpor", response.getData().getFirstName());
            assertEquals("Songkali", response.getData().getLastName());
            assertEquals("songkali@g.com", response.getData().getEmail());
            assertEquals("088999888777", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void deleteContactNotFound() throws JsonProcessingException, Exception{
        mockMvc.perform(
            delete("/api/contacts/21312341")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteContactSuccess() throws JsonProcessingException, Exception{
        User user = userRepository.findById("mike").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Johnson");
        contact.setLastName("Takpor");
        contact.setEmail("john@g.com");
        contact.setPhone("088111222333");
        contactRepository.save(contact);
        
        mockMvc.perform(
            delete("/api/contacts/" + contact.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNull(response.getErrors());
            assertEquals("OK",response.getData());


            assertFalse(contactRepository.existsById(contact.getId()));
        });
    }

    @Test
    void searchNotFound() throws JsonProcessingException, Exception{

        mockMvc.perform(
            get("/api/contacts/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }

    @Test
    void searchSuccess() throws JsonProcessingException, Exception{
        User user = userRepository.findById("mike").orElseThrow();

        for(int i=0; i<100; i++){
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("Johnson" + i);
            contact.setLastName("Takpor");
            contact.setEmail("john@g.com");
            contact.setPhone("088111222333");
            contactRepository.save(contact);
        }

        mockMvc.perform(
            get("/api/contacts/")
                .queryParam("name", "Johnson")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
            get("/api/contacts/")
                .queryParam("name", "Takpor")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
        
        mockMvc.perform(
            get("/api/contacts/")
                .queryParam("name", "Johnson")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
            get("/api/contacts/")
                .queryParam("email", "@g.com")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
            get("/api/contacts/")
                .queryParam("phone", "088111222333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
            get("/api/contacts/")
                .queryParam("page", "1000")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(1000, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }
    

}
