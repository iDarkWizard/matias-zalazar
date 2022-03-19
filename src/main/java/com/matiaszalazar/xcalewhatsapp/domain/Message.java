package com.matiaszalazar.xcalewhatsapp.domain;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity(name = "message")
@Table(name = "message")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
	
	public Message() {
		
	}

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinTable(name = "contact_message",
		joinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id"),
		uniqueConstraints  = {@UniqueConstraint(columnNames = {"message_id", "contact_id"})}
			)
	@JsonIgnoreProperties("messageList")
	private Contact source;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinTable(name = "conversation_message",
		joinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id"),
		uniqueConstraints  = {@UniqueConstraint(columnNames = {"message_id", "conversation_id"})}
			)
	@JsonIgnoreProperties("messageList")
	private Conversation conversation;
	
	private String content;

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Contact getSource() {
		return this.source;
	}

	public void setSource(Contact source) {
		this.source = source;
	}

	public Conversation getConversation() {
		return this.conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

}
