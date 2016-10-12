package com.upscale.front.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Anurag Garg
 *
 */

@Entity
@Table(name = "documents")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "documents")
public class Documents extends AbstractAuditingEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "document_type", nullable = false)
	private String documentType;

	@Column(name = "document_name", nullable = false)
	private String documentName;

	@Lob
	@Column(name = "document_image", nullable = false, columnDefinition = "mediumblob")
	private byte[] documentImage;

	@Column(name = "document_data")
	private String documentData;

	@Column(name = "document_id", nullable = true)
	private String documentId;

	@Column(name="content_type")
	private String contentType;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Documents() {}

public Documents(String documentType, String documentName, String documentId, String documentData,
		byte[] documentImage){
		this.documentType = documentType;
		this.documentName = documentName;
		this.documentId = documentId;
		this.documentData = documentData;
		this.documentImage = documentImage;
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

	public String getDocumentData() {
		return documentData;
	}

	public void setDocumentData(String documentData) {
		this.documentData = documentData;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Documents documents = (Documents) o;
		if (documents.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, documents.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Document{" + "id=" + id +
				", document Type='" + documentType + "'" +
				", document Name='" + documentName + "'" +
				", document Image='" + documentImage + "'" +
				", user='" + user + "'" + '}';
	}
}
