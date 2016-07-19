package com.upscale.front.repository;

import com.upscale.front.domain.SelfServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by saransh on 19/07/16.
 */
public interface SelfServiceRepository extends JpaRepository<SelfServiceUser, Long> {
}
