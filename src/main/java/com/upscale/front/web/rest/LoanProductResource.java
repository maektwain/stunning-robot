package com.upscale.front.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.upscale.front.domain.LoanProducts;
import com.upscale.front.domain.Tenant;
import com.upscale.front.repository.TenantsRepository;
import com.upscale.front.service.LoanProductsService;
import com.upscale.front.service.MifosBaseServices;
import com.upscale.front.web.rest.util.HeaderUtil;
import com.upscale.front.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoanProductResource {

	private final Logger log = LoggerFactory.getLogger(LoanProductResource.class);

	@Inject
	private LoanProductsService loanProductsService;

	@Inject
	private MifosBaseServices mifosBaseServices;

	@Inject
	private TenantsRepository tenantRepository;


	@RequestMapping(value = "/loanproducts",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> createLoanProducts(@RequestParam(value = "tenantName") String tenantName)  {

		log.debug("Rest request to save loan products : {}" );

        HttpHeaders textHttpHeaders = new HttpHeaders();

        textHttpHeaders.setContentType(MediaType.TEXT_PLAIN);

        /**
         * When provided the tenant name it should check whether  the tenant Exist or Not then Replay things
         */
        if (tenantName == null){
            return new ResponseEntity<>("Please add TenantName", textHttpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }



        Optional<Tenant> tenant = tenantRepository.findOneByTenantName(tenantName);

        if (tenant ==  null){
            return new ResponseEntity<Object>("The Tenant Does Not Exist", textHttpHeaders, HttpStatus.NOT_FOUND);
        }





        try {

            List<LoanProducts> loanProducts = mifosBaseServices.retrieveProduct("https://192.168.1.6:8443/fineract-provider/api/v1/loanproducts?tenantIdentifier="+ tenant.get().getTenant() +"&pretty=true", tenant.get().getId());
            for (LoanProducts products: loanProducts){
                System.out.println("Product Name: " +products.getName());
                loanProductsService.save(products);
            }
        }catch (UnirestException e){
            e.printStackTrace();
        }



        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert("loanproducts added for tenant", tenant.get().getTenant()))
            .body(null);
    }

	/**
     * SEARCH  /_search/loanproducts?query=:query : search for the loan products corresponding
     * to the query.
     *
     * @param query the query of the loan products search
     * @return the result of the search
	 * @throws URISyntaxException
     */

	@RequestMapping(value = "/_search/loanproducts",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LoanProducts>> searchLoanProducts(@RequestParam String query, Pageable pageable)
			throws URISyntaxException {
		log.debug("REST request to search for a page of loan products for query {}", query);
        Page<LoanProducts> page = loanProductsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/loanproducts");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}



}
