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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity(name = "contact")
@Table(name = "contact")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Contact {
	
	public Contact() {
		
	}

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", nullable = false)
	private UUID id;
	
	private String contactName;

	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "conversation_contact",
		joinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id"),
		uniqueConstraints  = {@UniqueConstraint(columnNames = {"contact_id", "conversation_id"})}
			)
	@JsonIgnoreProperties({"contactList", "messageList"})
	private List<Conversation> conversationList;
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "contact_message",
		joinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
		uniqueConstraints  = {@UniqueConstraint(columnNames = {"contact_id", "message_id"})}
			)
	@JsonIgnoreProperties("source")
	private List<Message> messageList;

//	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//	@JoinTable(name = "contact_message",
//		joinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id"),
//		inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
//		uniqueConstraints  = {@UniqueConstraint(columnNames = {"contact_id", "message_id"})}
//			)
//	@JsonIgnoreProperties("source")
	@Transient
	private List<Contact> friendList;

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public List<Conversation> getConversationList() {
		return this.conversationList;
	}

	public void setConversationList(List<Conversation> conversationList) {
		this.conversationList = conversationList;
	}

	public void addConversationToConversationList(Conversation conversation) {
		var conversationList = getConversationList().stream().collect(Collectors.toList());
		conversationList.add(conversation);
		setConversationList(conversationList);
	}

	public void removeConversationFromConversationList(Conversation conversation) {
		this.conversationList.remove(conversation);
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

	public List<Contact> getFriendsList() {
		return this.friendList;
	}

	public void setFriendList(List<Contact> friendList) {
		this.friendList = friendList;
	}

	public void addContactToFriendList(Contact contact) {
		this.friendList.add(contact);
	}

	public void removeContactFromFriendList(Contact contact) {
		this.friendList.remove(contact);
	}
}
