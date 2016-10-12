package com.upscale.front.repository;

import com.upscale.front.domain.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by saransh on 06/10/16.
 */
public interface OauthClientRepository extends JpaRepository<OauthClientDetails, Long> {



}
