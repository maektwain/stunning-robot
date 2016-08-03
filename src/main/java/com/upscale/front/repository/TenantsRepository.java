package com.upscale.front.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.upscale.front.domain.Tenant;

public interface TenantsRepository extends JpaRepository<Tenant, Long> {

    Tenant findOneByTenantName(String tenantName);


}
