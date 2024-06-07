package com.spring_restful.sandbox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.spring_restful.sandbox.entity.Contact;
import com.spring_restful.sandbox.entity.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> , JpaSpecificationExecutor<Contact> {
    
    Optional<Contact> findFirstByUserAndId(User user, String id);
}
