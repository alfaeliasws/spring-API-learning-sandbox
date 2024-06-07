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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_restful.sandbox.entity.Address;
import com.spring_restful.sandbox.entity.Contact;
import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.AddressResponse;
import com.spring_restful.sandbox.model.CreateAddressRequest;
import com.spring_restful.sandbox.model.UpdateAddressRequest;
import com.spring_restful.sandbox.model.WebResponse;
import com.spring_restful.sandbox.repository.AddressRepository;
import com.spring_restful.sandbox.repository.ContactRepository;
import com.spring_restful.sandbox.repository.UserRepository;
import com.spring_restful.sandbox.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp(){
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("mike");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("test");
        contact.setUser(user);
        contact.setFirstName("Johnson");
        contact.setLastName("Takpor");
        contact.setEmail("john@g.com");
        contact.setPhone("088111222333");
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadRequest() throws Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");
        
        mockMvc.perform(
            post("/api/contacts/test/address")
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
    void createAddressSuccess() throws Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("Indonesia");
        request.setCity("Jakarta");
        request.setProvince("DKI");
        request.setPostalCode("111111");
        request.setStreet("Jalan jalan");
        
        mockMvc.perform(
            post("/api/contacts/test/address")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});

            assertNull(response.getErrors());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("Jakarta", response.getData().getCity());
            assertEquals("DKI", response.getData().getProvince());
            assertEquals("111111", response.getData().getPostalCode());
            assertEquals("Jalan jalan", response.getData().getStreet());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getAddressNotFound() throws Exception{
        mockMvc.perform(
            get("/api/contacts/test/address/test")
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
    void getAddresssSuccess() throws Exception{
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setCountry("Indonesia");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setPostalCode("111111");
        address.setStreet("Jalan jalan");
        addressRepository.save(address);

        mockMvc.perform(
            get("/api/contacts/test/address/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> { 
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});

            assertNull(response.getErrors());
            assertEquals(address.getId(), response.getData().getId());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("Jakarta", response.getData().getCity());
            assertEquals("DKI", response.getData().getProvince());
            assertEquals("111111", response.getData().getPostalCode());
            assertEquals("Jalan jalan", response.getData().getStreet());
        });
    }

    @Test
    void updateAddressBadRequest() throws Exception{
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("");
        
        mockMvc.perform(
            put("/api/contacts/test/address/test")
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
    void upadateAddressSuccess() throws Exception{
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setCountry("Belgium");
        address.setCity("Brussel");
        address.setProvince("Ngawi");
        address.setPostalCode("111114");
        address.setStreet("Jalan jalan men");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("Indonesia");
        request.setCity("Jakarta");
        request.setProvince("DKI");
        request.setPostalCode("111111");
        request.setStreet("Jalan jalan");
        
        mockMvc.perform(
            put("/api/contacts/test/address/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>(){});

            assertNull(response.getErrors());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("Jakarta", response.getData().getCity());
            assertEquals("DKI", response.getData().getProvince());
            assertEquals("111111", response.getData().getPostalCode());
            assertEquals("Jalan jalan", response.getData().getStreet());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void deleteAddressNotFound() throws Exception{
        mockMvc.perform(
            delete("/api/contacts/test/address/test")
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
    void deleteAddresssSuccess() throws Exception{
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setCountry("Indonesia");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setPostalCode("111111");
        address.setStreet("Jalan jalan");
        addressRepository.save(address);

        mockMvc.perform(
            delete("/api/contacts/test/address/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> { 
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNull(response.getErrors());
            assertEquals("OK", response.getData());

            assertFalse(addressRepository.existsById("test"));
        });
    }

    @Test
    void ListAddressNotFound() throws Exception{
        mockMvc.perform(
            get("/api/contacts/salah/address/")
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
    void listAddresssSuccess() throws Exception{
        Contact contact = contactRepository.findById("test").orElseThrow();

        for(int i=0; i<5; i++){
            Address address = new Address();
            address.setId("test-"+i);
            address.setContact(contact);
            address.setCountry("Indonesia");
            address.setCity("Jakarta");
            address.setProvince("DKI");
            address.setPostalCode("111111");
            address.setStreet("Jalan jalan");
            addressRepository.save(address);
        }

        mockMvc.perform(
            get("/api/contacts/test/address/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> { 
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<AddressResponse>>>(){});

            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());
        });
    }

}
