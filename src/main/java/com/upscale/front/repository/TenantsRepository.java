package com.upscale.front.repository;

import com.upscale.front.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantsRepository extends JpaRepository<Tenant, Long> {

    Tenant findOneByTenantName(String tenantName);


}
