//package com.upscale.front.domain;
//
//import org.hibernate.annotations.Cache;
//import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.springframework.data.elasticsearch.annotations.Document;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
///**
// * Created by saransh on 22/07/16.
// */
//@Entity
//@Table(name = "jhi_product_by_user")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@Document(indexName = "user")
//public class ProductByUser extends AbstractAuditingEntity implements Serializable {
//
//    private static final Long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Column(name = "product_id")
//
//
//}
