package com.upscale.front.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.upscale.front.domain.Tenant;

public interface TenantsRepository extends JpaRepository<Tenant, Long> {
	

}
