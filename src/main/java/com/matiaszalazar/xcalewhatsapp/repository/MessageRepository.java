package com.matiaszalazar.xcalewhatsapp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiaszalazar.xcalewhatsapp.domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID>{

}
