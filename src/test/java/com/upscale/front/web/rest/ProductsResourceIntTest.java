package com.upscale.front.web.rest;

import com.upscale.front.FrontendApp;
import com.upscale.front.domain.Products;
import com.upscale.front.repository.ProductsRepository;
import com.upscale.front.service.ProductsService;
import com.upscale.front.repository.search.ProductsSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ProductsResource REST controller.
 *
 * @see ProductsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FrontendApp.class)
@WebAppConfiguration
@IntegrationTest
public class ProductsResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final String DEFAULT_BRAND = "AAAAA";
    private static final String UPDATED_BRAND = "BBBBB";
    private static final String DEFAULT_MODEL = "AAAAA";
    private static final String UPDATED_MODEL = "BBBBB";
    private static final String DEFAULT_IMAGE_URL = "AAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private ProductsRepository productsRepository;

    @Inject
    private ProductsService productsService;

    @Inject
    private ProductsSearchRepository productsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restProductsMockMvc;

    private Products products;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductsResource productsResource = new ProductsResource();
        ReflectionTestUtils.setField(productsResource, "productsService", productsService);
        this.restProductsMockMvc = MockMvcBuilders.standaloneSetup(productsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        productsSearchRepository.deleteAll();
        products = new Products();
        products.setName(DEFAULT_NAME);
        products.setPrice(DEFAULT_PRICE);
        products.setBrand(DEFAULT_BRAND);
        products.setModel(DEFAULT_MODEL);
        products.setImageUrl(DEFAULT_IMAGE_URL);
        products.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().size();

        // Create the Products

        restProductsMockMvc.perform(post("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(products)))
                .andExpect(status().isCreated());

        // Validate the Products in the database
        List<Products> products = productsRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = products.get(products.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testProducts.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testProducts.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProducts.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Products in ElasticSearch
        Products productsEs = productsSearchRepository.findOne(testProducts.getId());
        assertThat(productsEs).isEqualToComparingFieldByField(testProducts);
    }

    @Test
    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the products
        restProductsMockMvc.perform(get("/api/products?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
                .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
                .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get the products
        restProductsMockMvc.perform(get("/api/products/{id}", products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(products.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND.toString()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL.toString()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProducts() throws Exception {
        // Get the products
        restProductsMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProducts() throws Exception {
        // Initialize the database
        productsService.save(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products
        Products updatedProducts = new Products();
        updatedProducts.setId(products.getId());
        updatedProducts.setName(UPDATED_NAME);
        updatedProducts.setPrice(UPDATED_PRICE);
        updatedProducts.setBrand(UPDATED_BRAND);
        updatedProducts.setModel(UPDATED_MODEL);
        updatedProducts.setImageUrl(UPDATED_IMAGE_URL);
        updatedProducts.setDescription(UPDATED_DESCRIPTION);

        restProductsMockMvc.perform(put("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedProducts)))
                .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> products = productsRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = products.get(products.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testProducts.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testProducts.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProducts.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Products in ElasticSearch
        Products productsEs = productsSearchRepository.findOne(testProducts.getId());
        assertThat(productsEs).isEqualToComparingFieldByField(testProducts);
    }

    @Test
    @Transactional
    public void deleteProducts() throws Exception {
        // Initialize the database
        productsService.save(products);

        int databaseSizeBeforeDelete = productsRepository.findAll().size();

        // Get the products
        restProductsMockMvc.perform(delete("/api/products/{id}", products.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean productsExistsInEs = productsSearchRepository.exists(products.getId());
        assertThat(productsExistsInEs).isFalse();

        // Validate the database is empty
        List<Products> products = productsRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProducts() throws Exception {
        // Initialize the database
        productsService.save(products);

        // Search the products
        restProductsMockMvc.perform(get("/api/_search/products?query=id:" + products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
