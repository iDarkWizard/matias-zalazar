package com.matiaszalazar.xcalewhatsapp.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.matiaszalazar.xcalewhatsapp.domain.Conversation;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.service.ConversationService;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

	private final ConversationService conversationService;

	@Autowired
	public ConversationController(ConversationService conversationService) {
		this.conversationService = conversationService;
	}

	@PostMapping
	public ResponseEntity<?> postConversation(@RequestBody Conversation contact) {
		conversationService.createConversation(contact);
		return new ResponseEntity<>("Contact created.", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> getAllConversations() {
		return new ResponseEntity<>(conversationService.findAllConversations(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public Conversation getConversationById(@PathVariable UUID id) {
		try {
			return conversationService.findConversationById(id);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found. Id: ".concat(id.toString()));
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateMessage(@PathVariable UUID id, @RequestBody Conversation contact) {
		try {
			conversationService.updateConversation(contact);
			return new ResponseEntity<>("Conversation updated. Id: ".concat(id.toString()), HttpStatus.OK);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found. Id: ".concat(id.toString()));
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteConversation(@PathVariable UUID id) {
		try {
			conversationService.deleteConversation(conversationService.findConversationById(id));
			return new ResponseEntity<>("Conversation deleted.", HttpStatus.OK);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found. Id: ".concat(id.toString()));
		}
	}
}
