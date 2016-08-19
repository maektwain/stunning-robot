package com.upscale.front.repository;

import org.slf4j.LoggerFactory;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Collateral;
import com.upscale.front.domain.Tenant;

/***
 * 
 * @author Anurag Garg
 * 
 * Spring Data JPA repository for the Collateral entity.
 *
 */

@Repository
@Transactional
public interface CollateralRepository extends JpaRepository<Collateral, Long>{
	
	public final Logger log = LoggerFactory.getLogger(CollateralRepository.class);
	
	Optional<Collateral> findCollateralByTenantAndName(Tenant tenant, String name);
}
