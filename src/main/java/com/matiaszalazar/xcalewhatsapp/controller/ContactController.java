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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.matiaszalazar.xcalewhatsapp.domain.Contact;
import com.matiaszalazar.xcalewhatsapp.exception.NullIdException;
import com.matiaszalazar.xcalewhatsapp.exception.ResourceNotFoundException;
import com.matiaszalazar.xcalewhatsapp.service.ContactService;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

	private final ContactService contactService;

	@Autowired
	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@PostMapping("/{id}/add_to_conversation")
	public ResponseEntity<?> addContactToConversation(@PathVariable UUID id,
			@RequestParam(name = "conversationId", required = true) UUID conversationId) {
		contactService.addContactToConversation(id, conversationId);
		return new ResponseEntity<>("Contact added to conversation", HttpStatus.OK);
	}

	@PostMapping("{id}/send_message")
	public ResponseEntity<?> sendMessage(@PathVariable UUID id,
			@RequestParam(name = "conversationId", required = true) UUID conversationId,
			@RequestParam(name = "content", required = true) String content) {
		try {
			contactService.sendMessageToConversation(id, conversationId, content);
			return new ResponseEntity<>("Message sended to conversation", HttpStatus.OK);
		} catch(ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> postContact(@RequestBody Contact contact) {
		contactService.createContact(contact);
		return new ResponseEntity<>("Contact created.", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> getAllContacts() {
		return new ResponseEntity<>(contactService.findAllContacts(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public Contact getContactById(@PathVariable UUID id) {
		try {
			return contactService.findContactById(id);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found. Id: ".concat(id.toString()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateContact(@PathVariable UUID id, @RequestBody Contact contact) {
		try {
			contactService.updateContact(contact);
			return new ResponseEntity<>("Contact updated. Id: ".concat(id.toString()), HttpStatus.OK);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found. Id: ".concat(id.toString()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteContact(@PathVariable UUID id) {
		try {
			contactService.deleteContact(contactService.findContactById(id));
			return new ResponseEntity<>("Message deleted.", HttpStatus.OK);
		} catch (NullIdException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be null");
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found. Id: ".concat(id.toString()));
		}
	}
}
