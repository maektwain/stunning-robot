package com.upscale.front.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Documents;

@Repository
@Transactional
public interface DocumentsRepository extends JpaRepository<Documents, Long>{
	
	public final Logger log = LoggerFactory.getLogger(DocumentsRepository.class);
}