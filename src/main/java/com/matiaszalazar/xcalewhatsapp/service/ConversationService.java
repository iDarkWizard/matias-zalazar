package com.matiaszalazar.xcalewhatsapp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matiaszalazar.xcalewhatsapp.domain.Conversation;
import com.matiaszalazar.xcalewhatsapp.domain.Message;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.repository.ConversationRepository;

@Service
public class ConversationService {

	private final ConversationRepository conversationRepository;

	/**
	 * Constructor for ConversationService. The constructor will inject the services
	 * and repositories necessary for the operation of this service. On tests this
	 * is mocked.
	 * 
	 * @param conversationRepository {@link ConversationRepository}
	 */
	@Autowired
	public ConversationService(ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	/**
	 * Add a {@link Message} to the given {@link Conversation}. Then updates the conversation.
	 * 
	 * @param conversation {@link Conversation}
	 * @param message {@link Message}
	 */
	public void addMessageToConversation(Conversation conversation, Message message) {
		conversation.addMessageToMessageList(message);
		updateConversation(conversation);
	}

	/**
	 * Encapsulating repository behavior. Create {@link Conversation}
	 * 
	 * @param conversation {@link Conversation}
	 */
	public void createConversation(Conversation conversation) {
		conversationRepository.save(conversation);
	}

	/**
	 * Encapsulating repository behavior. Retrieves a list of {@link Conversation} Can return
	 * empty list.
	 * 
	 * @return conversationList {@link Conversation}
	 */
	public List<Conversation> findAllConversations() {
		return conversationRepository.findAll();
	}

	/**
	 * Encapsulating repository behavior. Retrieves a {@link Contact}.
	 * 
	 * @throws ResourceNotFoundException
	 * @param conversationId {@link UUID}
	 * @return {@link Conversation}
	 */
	public Conversation findConversationById(UUID conversationId) {
		if (conversationId == null)
			throw new NullIdException("The id must not be null");
		return conversationRepository.findById(conversationId).orElseThrow(
				() -> new ResourceNotFoundException("Conversation not found. Id: ".concat(conversationId.toString())));
	}

	/**
	 * Encapsulating repository behavior. Update a {@link Conversation}
	 * 
	 * @throws ResourceNotFoundException
	 * @param conversation {@link Conversation}
	 */
	public void updateConversation(Conversation conversation) {
		findConversationById(conversation.getId());
		conversationRepository.save(conversation);
	}

	/**
	 * Encapsulating repository behavior. Delete a {@link Conversation}
	 * 
	 * @throws ResourceNotFoundException
	 * @param conversation {@link Conversation}
	 */
	public void deleteConversation(Conversation conversation) {
		findConversationById(conversation.getId());
		conversationRepository.delete(conversation);
	}

}
