package com.upscale.front.repository;

import com.upscale.front.domain.Tenant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantsRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findOneByTenantName(String tenantName);

}
