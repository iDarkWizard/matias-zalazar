package com.matiaszalazar.xcalewhatsapp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matiaszalazar.xcalewhatsapp.domain.Message;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.repository.MessageRepository;

@Service
public class MessageService {

	private MessageRepository messageRepository;

	@Autowired
	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public void createMessage(Message message) {
		messageRepository.save(message);
	}

	public List<Message> findAllMessages() {
		return messageRepository.findAll();
	}

	public Message findMessageById(UUID messageId) {
		if (messageId == null)
			throw new NullIdException("The id must not be null");
		return messageRepository.findById(messageId).orElseThrow(
				() -> new ResourceNotFoundException("Message not found. Id: ".concat(messageId.toString())));
	}

	public void updateMessage(Message message) {
		findMessageById(message.getId());
		messageRepository.save(message);
	}

	public void deleteMessage(Message message) {
		findMessageById(message.getId());
		messageRepository.delete(message);
	}

}
