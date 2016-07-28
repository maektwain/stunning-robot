package com.upscale.front.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upscale.front.domain.LoanProducts;
/**
 * 
 * @author Anurag Garg
 * 
 * Spring Data JPA repository for the Loan Products entity.
 *
 */
@SuppressWarnings("unused")
public interface LoanProductsRepository extends JpaRepository<LoanProducts, Long> {

}
