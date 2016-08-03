package com.upscale.front.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.upscale.front.domain.LoanProducts;

/**
 * 
 * @author Anurag Garg
 * Spring Data ElasticSearch repository for the Loan Product entity.
 */
public interface LoanProductsSearchRepository extends ElasticsearchRepository<LoanProducts, Long>{

}
