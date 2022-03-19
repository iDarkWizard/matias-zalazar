package com.matiaszalazar.xcalewhatsapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;

import com.matiaszalazar.xcalewhatsapp.domain.Contact;
import com.matiaszalazar.xcalewhatsapp.domain.Conversation;
import com.matiaszalazar.xcalewhatsapp.domain.Message;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.repository.ConversationRepository;

public class ConversationServiceTest {

	private static final UUID CONVERSATION_UUID = UUID.fromString("ed28489e-113d-407d-a051-7e3351331b63");

	@Mock
	private ConversationRepository conversationRepository;

	private ConversationService conversationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		this.conversationService = new ConversationService(conversationRepository);
	}

	@Test
	void sendMessageToConversation() {
		Conversation conversation = new Conversation();
		conversation.setId(CONVERSATION_UUID);
		conversation.setConversationName("Test conversation");
		conversation.setMessageList(List.of());

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

		given(conversationRepository.findById(isA(UUID.class))).willReturn(Optional.of(conversation));

		conversationService.addMessageToConversation(conversation, message);
	}

	@Test
	void cannotFindConversationById() {
		try {
			conversationService.findConversationById(CONVERSATION_UUID);
		} catch (ResourceNotFoundException e) {
			var expected = "Conversation not found. Id: ".concat(CONVERSATION_UUID.toString());
			assertThat(e.getMessage()).isEqualTo(expected);
		}
	}

}
