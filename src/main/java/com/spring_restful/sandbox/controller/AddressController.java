package com.spring_restful.sandbox.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.spring_restful.sandbox.entity.User;
import com.spring_restful.sandbox.model.AddressResponse;
import com.spring_restful.sandbox.model.ContactResponse;
import com.spring_restful.sandbox.model.CreateAddressRequest;
import com.spring_restful.sandbox.model.UpdateAddressRequest;
import com.spring_restful.sandbox.model.WebResponse;
import com.spring_restful.sandbox.repository.AddressRepository;
import com.spring_restful.sandbox.service.AddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
public class AddressController {
    
    @Autowired
    private AddressService addressService;

    @PostMapping(
        path = "/api/contacts/{contactId}/address",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user, @RequestBody CreateAddressRequest request, @PathVariable("contactId") String contactId) {
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
        path = "/api/contacts/{contactId}/address/{addressId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse>  get(User user, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId) {
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping
    (
        path = "/api/contacts/{contactId}/address/{addressId}",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user, @RequestBody UpdateAddressRequest request, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId) {
        request.setContactId(contactId);
        request.setAddressId(addressId);

        AddressResponse addressResponse = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @DeleteMapping
    (
        path = "/api/contacts/{contactId}/address/{addressId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> remove(User user, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId) {
        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
        path = "/api/contacts/{contactId}/address/",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>>  list(User user, @PathVariable("contactId") String contactId) {
        List<AddressResponse> addressResponses = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(addressResponses).build();
    }

}
