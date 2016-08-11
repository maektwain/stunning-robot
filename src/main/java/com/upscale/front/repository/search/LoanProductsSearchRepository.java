package com.upscale.front.repository.search;

import com.upscale.front.domain.LoanProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author Anurag Garg
 * Spring Data ElasticSearch repository for the Loan Product entity.
 */
public interface LoanProductsSearchRepository extends ElasticsearchRepository<LoanProducts, Long>{


    /**
     * Find By DownPayment
     * @downPayment
     *
     */

    Page<LoanProducts> findByDownpaymentBetween(int downpayment, Pageable page);

}
