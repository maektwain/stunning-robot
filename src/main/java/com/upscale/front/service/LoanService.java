package com.upscale.front.service;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.Loan;
import com.upscale.front.repository.LoansRepository;

@Service
@Transactional
public class LoanService {

private final Logger log = LoggerFactory.getLogger(ClientService.class);
	
	@Inject
	private LoansRepository loansRepository;
	
	public Loan save(Loan loan) {
		log.debug("Request to save loan : {}", loan);
		Loan result = loansRepository.saveAndFlush(loan);
		return result;
	}
	
	@Transactional(readOnly = true)
	public Loan findOneByTenantAndUser(Long tenantId, Long userId) {
		log.debug("Request to get client : {}", tenantId, userId);
		Optional<Loan> loan = loansRepository.findOneByTenantAndUser(tenantId, userId);
		return loan.get();
		
	}
}
