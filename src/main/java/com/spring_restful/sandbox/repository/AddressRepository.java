package com.spring_restful.sandbox.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_restful.sandbox.entity.Address;
import com.spring_restful.sandbox.entity.Contact;

public interface AddressRepository extends JpaRepository<Address, String> {
    
    Optional<Address> findFirstByContactAndId(Contact contact, String id);

    List<Address> findAllByContact(Contact contact);
}
