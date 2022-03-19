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

import com.matiaszalazar.xcalewhatsapp.domain.Message;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.service.MessageService;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

	private final MessageService messageService;

	@Autowired
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping
	public ResponseEntity<?> postMessage(@RequestBody Message message) {
		messageService.createMessage(message);
		return new ResponseEntity<>("Message created.", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> getAllMessages() {
		return new ResponseEntity<>(messageService.findAllMessages(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public Message getMessageById(@PathVariable UUID id) {
		try {
			return messageService.findMessageById(id);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found. Id: ".concat(id.toString()));
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateMessage(@PathVariable UUID id, @RequestBody Message message) {
		try {
			messageService.updateMessage(message);
			return new ResponseEntity<>("Message updated. Id: ".concat(id.toString()), HttpStatus.OK);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found. Id: ".concat(id.toString()));
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteMessage(@PathVariable UUID id) {
		try {
			messageService.deleteMessage(messageService.findMessageById(id));
			return new ResponseEntity<>("Message deleted.", HttpStatus.OK);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found. Id: ".concat(id.toString()));
		}
	}
}
