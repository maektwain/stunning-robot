package com.upscale.front.repository;

import com.upscale.front.domain.OauthClientDetails;
import com.upscale.front.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by saransh on 06/10/16.
 */
@Repository
@Transactional
public interface OauthClientRepository extends JpaRepository<OauthClientDetails, Long> {

    public final Logger log = LoggerFactory.getLogger(OauthClientRepository.class);

    Optional<OauthClientDetails> findAllByUser(User u);

    Optional<OauthClientDetails> findOneByApplicationNameAndUser(String applicationName, User userId);

}
