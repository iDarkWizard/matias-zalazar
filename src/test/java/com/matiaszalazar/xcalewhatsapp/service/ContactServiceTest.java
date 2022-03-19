package com.matiaszalazar.xcalewhatsapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.matiaszalazar.xcalewhatsapp.domain.Contact;
import com.matiaszalazar.xcalewhatsapp.domain.Conversation;
import com.matiaszalazar.xcalewhatsapp.domain.Message;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.repository.ContactRepository;

public class ContactServiceTest {

	private static final UUID CONTACT_UUID = UUID.fromString("ed28489e-113d-407d-a051-7e3351331b64");
	private static final UUID CONVERSATION_UUID = UUID.fromString("ed28489e-113d-407d-a051-7e3351331b63");

	@Mock
	private ConversationService conversationService;

	@Mock
	private ContactRepository contactRepository;
	
	@Mock
	private MessageService messageService;

	private ContactService contactService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		this.contactService = new ContactService(conversationService, contactRepository, messageService);
	}

	@Test
	void sendMessageToConversation() {
		Conversation conversation = new Conversation();
		conversation.setId(CONVERSATION_UUID);
		conversation.setConversationName("Test conversation");

		List<Message> messageList = List.of();

		Contact source = new Contact();
		source.setId(CONTACT_UUID);
		source.setContactName("Matias Zalazar");
		source.setConversationList(List.of(conversation));
		source.setMessageList(messageList);
		source.setFriendList(List.of());

		conversation.setContactList(List.of(source));

		Message message = new Message();
		message.setContent("Test content");
		message.setConversation(conversation);
		message.setSource(source);

		given(contactRepository.findById(CONTACT_UUID)).willReturn(Optional.of(source));
		given(conversationService.findConversationById(isA(UUID.class))).willReturn(conversation);

		contactService.sendMessage(CONTACT_UUID, message);
	}

	@Test
	void sendMessageNotFoundContact() {
		Conversation conversation = new Conversation();
		conversation.setId(CONVERSATION_UUID);
		conversation.setConversationName("Test conversation");

		List<Message> messageList = List.of();

		Contact source = new Contact();
		source.setContactName("Matias Zalazar");
		source.setConversationList(List.of(conversation));
		source.setMessageList(messageList);
		source.setFriendList(List.of());

		conversation.setContactList(List.of(source));

		Message message = new Message();
		message.setContent("Test content");
		message.setConversation(conversation);
		message.setSource(source);

		try {
			contactService.sendMessage(CONTACT_UUID, message);
		} catch (ResourceNotFoundException e) {
			var expected = "Contact not found. Id: ".concat(CONTACT_UUID.toString());
			assertThat(e.getMessage()).isEqualTo(expected);
		}
	}
}
