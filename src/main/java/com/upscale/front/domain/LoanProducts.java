package com.upscale.front.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Table(name = "loan_products")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "loan_products")
public class LoanProducts implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "tenant_id")
    private Long tenantId;
    
    @Column(name = "principal")
    private BigDecimal principal;
    
    @Column(name = "max_principal")
    private BigDecimal maxPrincipal;
    
    @Column(name = "min_principal")
    private BigDecimal minPrincipal;
    
    /*@Column(name = "downpayment")
    private BigDecimal downPayment;
    
    @Column(name = "max_downpayment")
    private BigDecimal maxDownPayment;
    
    @Column(name = "min_downpayment")
    private BigDecimal minDownPayment;*/
    
    @Column(name = "tenure")
    private int tenure;
    
    @Column(name = "max_tenure")
    private int maxTenure;
    
    @Column(name = "min_tenure")
    private int minTenure;
    
    @Column(name = "interest_rate")
    private BigDecimal interest;
    
    @Column(name = "max_interest_rate")
    private BigDecimal maxInterest;
    
    @Column(name = "min_interest_rate")
    private BigDecimal minInterest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getMaxPrincipal() {
		return maxPrincipal;
	}

	public void setMaxPrincipal(BigDecimal maxPrincipal) {
		this.maxPrincipal = maxPrincipal;
	}

	public BigDecimal getMinPrincipal() {
		return minPrincipal;
	}

	public void setMinPrincipal(BigDecimal minPrincipal) {
		this.minPrincipal = minPrincipal;
	}

	/*public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getMaxDownPayment() {
		return maxDownPayment;
	}

	public void setMaxDownPayment(BigDecimal maxDownPayment) {
		this.maxDownPayment = maxDownPayment;
	}

	public BigDecimal getMinDownPayment() {
		return minDownPayment;
	}

	public void setMinDownPayment(BigDecimal minDownPayment) {
		this.minDownPayment = minDownPayment;
	}*/

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public int getMaxTenure() {
		return maxTenure;
	}

	public void setMaxTenure(int maxTenure) {
		this.maxTenure = maxTenure;
	}

	public int getMinTenure() {
		return minTenure;
	}

	public void setMinTenure(int minTenure) {
		this.minTenure = minTenure;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public BigDecimal getMaxInterest() {
		return maxInterest;
	}

	public void setMaxInterest(BigDecimal maxInterest) {
		this.maxInterest = maxInterest;
	}

	public BigDecimal getMinInterest() {
		return minInterest;
	}

	public void setMinInterest(BigDecimal minInterest) {
		this.minInterest = minInterest;
	}
    
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoanProducts loanProducts = (LoanProducts) o;
        if(loanProducts.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, loanProducts.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "LoanProducts{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", tenant='" + tenantId + "'" +
            ", principal='" + principal + "'" +
            ", maxPrincipal='" + maxPrincipal + "'" +
            ", minPrincipal='" + minPrincipal + "'" +
            /*", downpayment='" + downPayment + "'" +
            ", maxDownpayment='" + maxDownPayment + "'" +
            ", minDownpayment='" + minDownPayment + "'" +*/
            ", tenure='" + tenure + "'" +
            ", maxTenure='" + maxTenure + "'" +
            ", minTenure='" + minTenure + "'" +
            ", interest='" + interest + "'" +
            ", maxInterest='" + maxInterest + "'" +
            ", minInterest='" + minInterest + "'" +
            '}';
    }
}
