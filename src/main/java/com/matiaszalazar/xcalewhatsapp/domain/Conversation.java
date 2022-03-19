package com.matiaszalazar.xcalewhatsapp.domain;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Conversation entity. Contains - id: {@linkplain UUID} - conversationName
 * {@link String} - contactList {@link List} {@link Contact} - messageList
 * {@link List} {@link Message}
 * 
 * @author Matias Zalazar
 *
 */
@Entity(name = "conversation")
@Table(name = "conversation")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Conversation {

	public Conversation() {

	}

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", nullable = false)
	private UUID id;

	private String conversationName;

	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "conversation_contact",
		joinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id"),
		uniqueConstraints  = {@UniqueConstraint(columnNames = {"conversation_id", "contact_id"})}
			)
	@JsonIgnoreProperties({"conversationList", "messageList"})
	private List<Contact> contactList;

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "conversation_message",
		joinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
		uniqueConstraints  = {@UniqueConstraint(columnNames = {"conversation_id", "message_id"})}
			)
	@JsonIgnoreProperties("conversation")
	private List<Message> messageList;

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getConversationName() {
		return this.conversationName;
	}

	public void setConversationName(String conversationName) {
		this.conversationName = conversationName;
	}

	public List<Contact> getContactList() {
		return this.contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}

	public void addContactToContactList(Contact contact) {
		var contactList = getContactList().stream().collect(Collectors.toList());
		contactList.add(contact);
		setContactList(contactList);
	}

	public void removeContactFromContactList(Contact contact) {
		this.contactList.remove(contact);
	}

	public List<Message> getMessageList() {
		return this.messageList;
	}

	public void setMessageList(List<Message> messageList) {
		this.messageList = messageList;
	}

	public void addMessageToMessageList(Message message) {
		var messageList = getMessageList().stream().collect(Collectors.toList());
		messageList.add(message);
		setMessageList(messageList);
	}

	public void removeMessageFromMessageList(Message message) {
		var messageList = getMessageList().stream().collect(Collectors.toList());
		messageList.remove(message);
		setMessageList(messageList);
	}
}
