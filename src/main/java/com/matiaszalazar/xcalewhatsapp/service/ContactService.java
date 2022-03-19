package com.matiaszalazar.xcalewhatsapp.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matiaszalazar.xcalewhatsapp.domain.Contact;
import com.matiaszalazar.xcalewhatsapp.domain.Conversation;
import com.matiaszalazar.xcalewhatsapp.domain.Message;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.repository.ContactRepository;

@Service
public class ContactService {

	private Logger log = LoggerFactory.getLogger(ContactService.class);

	private final ConversationService conversationService;

	private final MessageService messageService;

	private final ContactRepository contactRepository;

	/**
	 * Constructor for ContactService. The constructor will inject the services and
	 * repositories necessary for the operation of this service. On tests this is
	 * mocked.
	 * 
	 * @param conversationService
	 * @param contactRepository
	 * @param messageService
	 */
	@Autowired
	public ContactService(ConversationService conversationService, ContactRepository contactRepository,
			MessageService messageService) {
		this.conversationService = conversationService;
		this.contactRepository = contactRepository;
		this.messageService = messageService;
	}

	/**
	 * This method builds a {@link Message} with the given contactId and conversationId.
	 * 
	 * @throws ResourceNotFoundException
	 * @param contactId {@link UUID}
	 * @param conversationId {@link UUID}
	 * @param content {@link String}
	 */
	public void sendMessageToConversation(UUID contactId, UUID conversationId, String content) {
		var contact = findContactById(contactId);
		var conversation = conversationService.findConversationById(conversationId);
		if (!contact.getConversationList().stream().anyMatch(con -> con.getId().equals(conversationId)))
			throw new ResourceNotFoundException(String.format("Conversation %s not found for the given contact: %s",
					contactId.toString(), conversationId.toString()));
		var message = new Message();
		message.setContent(content);
		message.setConversation(conversation);
		message.setSource(contact);
		messageService.createMessage(message);

		sendMessage(contactId, message);
	}

	/**
	 * This method updates the {@link Contact} and the {@link Conversation} adding the recently
	 * generated {@link Message}. Then the method send the notifications.
	 * 
	 * @throws ResourceNotFoundException
	 * @param from {@link UUID}
	 * @param message {@link Message}
	 */
	public void sendMessage(UUID from, Message message) {
		Contact contact = findContactById(from);
//		contact.addMessageToMessageList(message);
//		updateContact(contact);
//
		var conversationId = message.getConversation().getId();
		Conversation conversation = conversationService.findConversationById(conversationId);
		conversationService.addMessageToConversation(conversation, message);

		notifieMessage(contact.getContactName(), conversation.getConversationName(), message);
	}

	/**
	 * Enable the {@link Contact} to chat on an existing {@link Conversation}.
	 * Save the information on both sides.
	 * 
	 * @throws ResourceNotFoundException
	 * @param id {@link UUID}
	 * @param conversationId {@link UUID}
	 */
	public void addContactToConversation(UUID id, UUID conversationId) {
		var contact = findContactById(id);
		var conversation = conversationService.findConversationById(conversationId);
		contact.addConversationToConversationList(conversation);
		contactRepository.save(contact);
		conversation.addContactToContactList(contact);
		conversationService.updateConversation(conversation);
	}

	/**
	 * Encapsulating repository behavior. Save a {@link Contact}.
	 * 
	 * @param contact {@link Contact}
	 */
	public void createContact(Contact contact) {
		contactRepository.save(contact);
	}

	/**
	 * Encapsulating repository behavior. Retrieves a list of {@link Contact}. Can return
	 * empty list.
	 * 
	 * @return contactList {@link Contact}
	 */
	public List<Contact> findAllContacts() {
		return contactRepository.findAll();
	}

	/**
	 * Encapsulating repository behavior. Retrieves a {@link Contact}.
	 * 
	 * @throws ResourceNotFoundException {@link NullIdException}
	 * @param contactId {@link UUID}
	 * @return contact {@link Contact}
	 */
	public Contact findContactById(UUID contactId) {
		if (contactId == null)
			throw new NullIdException("The id must not be null");
		return contactRepository.findById(contactId).orElseThrow(
				() -> new ResourceNotFoundException("Contact not found. Id: ".concat(contactId.toString())));
	}

	/**
	 * Encapsulating repository behavior. Update a {@link Contact}.
	 * 
	 * @throws ResourceNotFoundException
	 * @param contact {@link Contact}
	 */
	public void updateContact(Contact contact) {
		findContactById(contact.getId());
		contactRepository.save(contact);
	}

	/**
	 * Encapsulating repository behavior. Delete a {@link Contact}.
	 * 
	 * @throws ResourceNotFoundException
	 * @param contact {@link Contact}
	 */
	public void deleteContact(Contact contact) {
		findContactById(contact.getId());
		contactRepository.delete(contact);
	}

	/**
	 * This method notifies all the users of the recently added {@link Message}.
	 * 
	 * @param source {@link String}
	 * @param destination {@link String}
	 * @param message {@link Message}
	 */
	private void notifieMessage(String source, String destination, Message message) {
		log.info("Notification implementation: ");
		log.info("{} envió un mensaje a {}: ", source, destination);
		log.info(message.getContent());
	}
}
