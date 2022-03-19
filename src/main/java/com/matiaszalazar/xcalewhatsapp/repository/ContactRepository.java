package com.matiaszalazar.xcalewhatsapp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiaszalazar.xcalewhatsapp.domain.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

}
