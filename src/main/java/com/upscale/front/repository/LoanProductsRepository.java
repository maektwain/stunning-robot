package com.upscale.front.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.LoanProducts;
/**
 * 
 * @author Anurag Garg
 * 
 * Spring Data JPA repository for the Loan Products entity.
 *
 */
@Repository
@Transactional
public interface LoanProductsRepository extends JpaRepository<LoanProducts, Long> {
	
	public final Logger log = LoggerFactory.getLogger(LoanProductsRepository.class);

}
