package com.upscale.front.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Client;
import com.upscale.front.domain.Tenant;
import com.upscale.front.domain.User;
import com.upscale.front.repository.ClientsRepository;

/**
 * 
 * @author Anurag Garg
 *
 */
@Service
@Transactional
public class ClientService {
	
	private final Logger log = LoggerFactory.getLogger(ClientService.class);
	
	@Inject
	private ClientsRepository clientsRepository;
	
	public Client save(Client client) {
		log.debug("Request to save client : {}", client);
		Client result = clientsRepository.saveAndFlush(client);
		return result;
	}
	
	@Transactional(readOnly = true)
	public Optional<Client> findOneByTenantAndUser(Tenant tenantId, User userId) {
		log.debug("Request to get client : {}", tenantId, userId);
		Optional<Client> client = clientsRepository.findOneByTenantAndUser(tenantId, userId);
		return client;
		
	}
	
	@Transactional(readOnly = true)
	public List<Client> findByUser(User userId) {
		log.debug("Request to get client list based on userId : {}", userId);
		List<Client> client = clientsRepository.findByUser(userId);
		return client;
	}

}
