package com.upscale.front.web.rest.dto;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.upscale.front.domain.AbstractAuditingEntity;
import com.upscale.front.domain.Documents;
import com.upscale.front.domain.User;

public class DocumentDTO {
	
	
	private Long id;

	private String documentType;
	
	private String documentName;
	
	private byte[] documentImage;
	
	private String contentType;
	
	private User user;

	public DocumentDTO() {
		
	}
	
	public DocumentDTO(Documents doc) {
		this(doc.getId(), doc.getDocumentType(), doc.getDocumentName(), doc.getDocumentImage(),
				doc.getContentType(), doc.getUser());
	}
	
	public DocumentDTO(Long id, String documentType, String documentName, byte[] documentImage,
			String contentType, User user){
		this.id = id;
		this.documentType = documentType;
		this.documentName = documentName;
		this.documentImage = documentImage;
		this.contentType = contentType;
		this.user = user;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public byte[] getDocumentImage() {
		return documentImage;
	}

	public void setDocumentImage(byte[] documentImage) {
		this.documentImage = documentImage;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public String toString() {
		return "DocumentDTO{" + "id=" + id + 
				", document Type='" + documentType + "'" + 
				", document Name='" + documentName + "'" + 
				", document Image='" + documentImage + "'" +
				", user='" + user + "'" + '}';
	}
}
