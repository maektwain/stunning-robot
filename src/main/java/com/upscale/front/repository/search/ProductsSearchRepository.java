package com.upscale.front.repository.search;

import com.upscale.front.domain.Products;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Products entity.
 */
public interface ProductsSearchRepository extends ElasticsearchRepository<Products, Long> {
}
