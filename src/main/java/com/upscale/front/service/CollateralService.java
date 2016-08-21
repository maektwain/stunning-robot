package com.upscale.front.service;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.CollateralData;
import com.upscale.front.domain.Tenant;
import com.upscale.front.repository.CollateralRepository;

/**
 * 
 * @author Anurag Garg
 *
 */

@Service
@Transactional
public class CollateralService {
	
	private final Logger log = LoggerFactory.getLogger(CollateralService.class);

	@Inject
	private CollateralRepository collateralRepository;

	public CollateralData save(CollateralData collateral) {
		log.debug("Request to save Collateral : {}", collateral);
		CollateralData result = collateralRepository.saveAndFlush(collateral);
		return result;
	}

	@Transactional(readOnly = true)
	public Page<CollateralData> findAll(Pageable pageable) {
		log.debug("Request to get all Collaterals");
		Page<CollateralData> result = collateralRepository.findAll(pageable);
		return result;
	}

	@Transactional(readOnly = true)
	public CollateralData findOne(Long id) {
		log.debug("Request to get collateral : {}", id);
		CollateralData collateral = collateralRepository.findOne(id);
		return collateral;
	}

	@Transactional(readOnly = true)
	public CollateralData findCollateralByTenantAndName(Tenant tenant, String name) {
		log.debug("Request to get collateral by Tenant and Name : {}", tenant.getTenant(), name);
		Optional<CollateralData> collateral = collateralRepository.findCollateralByTenantAndName(tenant, name);
		return collateral.get();
	}
	public void delete(Long id) {
		log.debug("Request to delete Collateral : {}", id);
		collateralRepository.delete(id);
	}
}
