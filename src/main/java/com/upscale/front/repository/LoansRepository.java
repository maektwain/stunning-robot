package com.upscale.front.repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Loan;
import com.upscale.front.domain.User;

/**
 * 
 * @author Anurag Garg
 *
 */

@Repository
@Transactional
public interface LoansRepository extends JpaRepository<Loan, Long> {
	
	public final Logger log = LoggerFactory.getLogger(LoansRepository.class);
	
	Optional<Loan> findOneByTenantAndUser(Long tenantId, Long userId);
	
	Optional<List<Loan>> findByUser(User userId);

}
