package com.matiaszalazar.xcalewhatsapp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiaszalazar.xcalewhatsapp.domain.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID>{

}
