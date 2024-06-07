package com.spring_restful.sandbox.service;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_restful.sandbox.entity.Address;
import com.spring_restful.sandbox.entity.Contact;
import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.AddressResponse;
import com.spring_restful.sandbox.model.ContactResponse;
import com.spring_restful.sandbox.model.CreateAddressRequest;
import com.spring_restful.sandbox.model.UpdateAddressRequest;
import com.spring_restful.sandbox.repository.AddressRepository;
import com.spring_restful.sandbox.repository.ContactRepository;
import com.spring_restful.sandbox.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AddressService {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    private AddressResponse toAddressResponse(Address address){
        return AddressResponse.builder()
        .id(address.getId())
        .street(address.getStreet())
        .city(address.getCity())
        .country(address.getCountry())
        .province(address.getProvince())
        .postalCode(address.getPostalCode())
        .build();
    }


    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found")
        );

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setProvince(request.getProvince());
        addressRepository.save(address);

        return toAddressResponse(address);

    }

    @Transactional
    public AddressResponse get(User user, String contactId, String addressId){

        Contact contact = contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found")
        );

        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adress Not Found"));

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user,request.getContactId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found")
        );

        Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adress Not Found"));

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setProvince(request.getProvince());
        addressRepository.save(address);

        return toAddressResponse(address);

    }

    @Transactional
    public void remove(User user, String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found")
        );

        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adress Not Found"));

        addressRepository.delete(address);
    }

    @Transactional
    public List<AddressResponse> list(User user, String contactId){
        Contact contact = contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found")
        );

        List<Address> addresses = addressRepository.findAllByContact(contact);
        
        return addresses.stream().map(this::toAddressResponse).toList();
    }
}
