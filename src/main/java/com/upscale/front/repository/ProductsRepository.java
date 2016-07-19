package com.upscale.front.repository;

import com.upscale.front.domain.Products;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Products entity.
 */
@SuppressWarnings("unused")
public interface ProductsRepository extends JpaRepository<Products,Long> {

}
