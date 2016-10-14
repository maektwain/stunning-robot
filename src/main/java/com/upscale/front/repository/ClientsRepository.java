package com.upscale.front.repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Client;
import com.upscale.front.domain.Tenant;
import com.upscale.front.domain.User;

/**
 * 
 * @author Anurag Garg
 *
 */

@Repository
@Transactional
public interface ClientsRepository extends JpaRepository<Client	, Long> {

	public final Logger log = LoggerFactory.getLogger(ClientsRepository.class);

	Optional<Client> findOneByTenantAndUser(Tenant tenantId, User userId);
	
	List<Client> findByUser(User userId);
}