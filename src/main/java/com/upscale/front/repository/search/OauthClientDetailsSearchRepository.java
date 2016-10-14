package com.upscale.front.repository.search;

import com.upscale.front.domain.OauthClientDetails;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by saransh on 06/10/16.
 */
public interface OauthClientDetailsSearchRepository extends ElasticsearchRepository<OauthClientDetails, Long> {



}
