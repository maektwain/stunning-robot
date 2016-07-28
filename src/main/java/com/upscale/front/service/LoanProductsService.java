package com.upscale.front.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upscale.front.domain.LoanProducts;
import com.upscale.front.repository.LoanProductsRepository;
import com.upscale.front.repository.search.LoanProductsSearchRepository;

@Service
@Transactional
public class LoanProductsService {
	
	private final Logger log = LoggerFactory.getLogger(LoanProductsService.class);
	
	@Inject
	private LoanProductsRepository loanProductsRepository;
	
	@Inject
	private LoanProductsSearchRepository loanProductsSearchRepository;
	
	public LoanProducts save(LoanProducts loanProducts) {
		log.debug("Request to save Loan Products : {}", loanProducts);
		LoanProducts result  = loanProductsRepository.save(loanProducts);
		loanProductsSearchRepository.save(result);
		return result;
	}
	
	@Transactional(readOnly = true)
	public Page<LoanProducts> findAll(Pageable pageable) {
		log.debug("Request to get all Loan Products");
		Page<LoanProducts> result = loanProductsRepository.findAll(pageable);
		return result;
	}
	
	@Transactional(readOnly = true)
	public LoanProducts findOne(Long id) {
		log.debug("Request to get loan products : {}", id);
		LoanProducts loanProducts = loanProductsRepository.findOne(id);
		return loanProducts;
	}
	
	public void delete(Long id) {
		log.debug("Request to delete Loan Products : {}", id);
		loanProductsRepository.delete(id);
		loanProductsSearchRepository.delete(id);
	}
	
	@Transactional(readOnly = true)
	public Page<LoanProducts> search(String query, Pageable pageable) {
		log.debug("Request to search for a page of Products for query {}", query);
		return loanProductsSearchRepository.search(queryStringQuery(query), pageable);
	}

}

