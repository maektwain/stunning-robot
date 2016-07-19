package com.upscale.front.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.upscale.front.domain.Products;
import com.upscale.front.service.ProductsService;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Products.
 */
@RestController
@RequestMapping("/api")
public class ProductsResource {

    private final Logger log = LoggerFactory.getLogger(ProductsResource.class);

    @Inject
    private ProductsService productsService;

    /**
     * POST  /products : Create a new products Only an Admin Can Create IT.
     *
     * @param products the products to create
     * @return the ResponseEntity with status 201 (Created) and with body the new products, or with status 400 (Bad Request) if the products has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Secured(value = "ROLE_ADMIN")
    @RequestMapping(value = "/products",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Products> createProducts(@RequestBody Products products) throws URISyntaxException {


        log.debug("REST request to save Products : {}", products);

        if (products.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("products", "idexists", "A new products cannot already have an ID")).body(null);
        }
        Products result = productsService.save(products);
        return ResponseEntity.created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("products", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /products : Updates an existing products.
     *
     * @param products the products to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated products,
     * or with status 400 (Bad Request) if the products is not valid,
     * or with status 500 (Internal Server Error) if the products couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Secured(value = "ROLE_ADMIN")
    @RequestMapping(value = "/products",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Products> updateProducts(@RequestBody Products products) throws URISyntaxException {
        log.debug("REST request to update Products : {}", products);
        if (products.getId() == null) {
            return createProducts(products);
        }
        Products result = productsService.save(products);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("products", products.getId().toString()))
            .body(result);
    }

    /**
     * GET  /products : get all the products.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of products in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */

    @RequestMapping(value = "/products",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Products>> getAllProducts(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Products");
        Page<Products> page = productsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/products");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /products/:id : get the "id" products.
     *
     * @param id the id of the products to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the products, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/products/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Products> getProducts(@PathVariable Long id) {
        log.debug("REST request to get Products : {}", id);
        Products products = productsService.findOne(id);
        return Optional.ofNullable(products)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /products/:id : delete the "id" products.
     *
     * @param id the id of the products to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Secured(value = "ROLE_ADMIN")
    @RequestMapping(value = "/products/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteProducts(@PathVariable Long id) {
        log.debug("REST request to delete Products : {}", id);
        productsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("products", id.toString())).build();
    }

    /**
     * SEARCH  /_search/products?query=:query : search for the products corresponding
     * to the query.
     *
     * @param query the query of the products search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/products",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Products>> searchProducts(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Products for query {}", query);
        Page<Products> page = productsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/products");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
