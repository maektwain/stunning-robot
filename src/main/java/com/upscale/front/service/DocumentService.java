package com.upscale.front.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Documents;
import com.upscale.front.domain.User;
import com.upscale.front.repository.DocumentsRepository;

/**
 * 
 * @author Anurag Garg
 *
 */
@Service
@Transactional
public class DocumentService {
	
	private final Logger log = LoggerFactory.getLogger(DocumentService.class);
	
	@Inject
	private DocumentsRepository documentsRepository;
	
	public Documents save(Documents documents) {
		log.debug("Request to save Documents : {}", documents);
		Documents result = documentsRepository.saveAndFlush(documents);
		return result;
	}

	@Transactional(readOnly = true)
	public List<Documents> findAll() {
		log.debug("Request to get all documents");
		List<Documents> result = documentsRepository.findAll();
		return result;
	}
	
	@Transactional(readOnly = true)
	public Optional<List<Documents>> findAllByUser(User u) {
		log.debug("Request to get all Documents by user: {}", u );
		Optional<List<Documents>> result = documentsRepository.findAllByUserId(u.getId());
		return result;
	}
	
	@Transactional(readOnly = true)
	public Optional<Documents> findByDocumentTypeAndUser(String documentType, User u) {
		log.debug("Request to get Document by user: {}", u );
		Optional<Documents> result = documentsRepository.findByDocumentTypeAndUser(documentType, u);
		return result;
	}
	
	 /**
     *  Delete the documents by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Document : {}", id);
        documentsRepository.delete(id);
    }
}
