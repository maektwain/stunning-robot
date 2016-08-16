package com.upscale.front.service;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Tenant;
import com.upscale.front.repository.TenantsRepository;

@Service
@Transactional
public class TenantService {

	public final Logger log = LoggerFactory.getLogger(TenantService.class);
	
	@Inject
	private TenantsRepository tenantsRepository;
	
	public Tenant save(Tenant tenant) {
		log.debug("Request to save Tenant : {}", tenant);
		Tenant result = tenantsRepository.save(tenant);
		return result;
	}
	
	@Transactional(readOnly = true)
	public Tenant findOne(Long id) {
		log.debug("Request to get tenant : {}", id);
		Tenant tenant = tenantsRepository.findOne(id);
		return tenant;
	}
	
	@Transactional(readOnly = true)
	public Optional<Tenant> findOneByTenantName(String tenantName) {
		log.debug("Request to get tenant : {}", tenantName);
		Optional<Tenant> tenant = tenantsRepository.findOneByTenantName(tenantName);
		return tenant;
	}
}
