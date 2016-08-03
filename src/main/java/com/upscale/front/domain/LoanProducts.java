package com.upscale.front.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "loan_products")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "loan_products")
public class LoanProducts extends AbstractAuditingEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;




    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Id

    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;


    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Tenant tenant;


    @Column(name = "principal")
    private BigDecimal principal;

    @Column(name = "max_principal")
    private BigDecimal maxPrincipal;

    @Column(name = "min_principal")
    private BigDecimal minPrincipal;

    @Column(name = "downpayment")
    private BigDecimal downpayment;

    @Column(name = "max_downpayment")
    private BigDecimal maxDownpayment;

    @Column(name = "min_downpayment")
    private BigDecimal minDownpayment;

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

    @Column(name = "description")
    private String description;

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

	public BigDecimal getDownpayment() {
		return downpayment;
	}

	public void setDownpayment(BigDecimal downpayment) {
		this.downpayment = downpayment;
	}

	public BigDecimal getMaxDownpayment() {
		return maxDownpayment;
	}

	public void setMaxDownpayment(BigDecimal maxDownpayment) {
		this.maxDownpayment = maxDownpayment;
	}

	public BigDecimal getMinDownpayment() {
		return minDownpayment;
	}

	public void setMinDownpayment(BigDecimal minDownpayment) {
		this.minDownpayment = minDownpayment;
	}

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

	public String getDescription(){
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
            ", tenant='" + tenant + "'" +
            ", principal='" + principal + "'" +
            ", maxPrincipal='" + maxPrincipal + "'" +
            ", minPrincipal='" + minPrincipal + "'" +
            ", downpayment='" + downpayment + "'" +
            ", maxDownpayment='" + maxDownpayment + "'" +
            ", minDownpayment='" + minDownpayment + "'" +
            ", tenure='" + tenure + "'" +
            ", maxTenure='" + maxTenure + "'" +
            ", minTenure='" + minTenure + "'" +
            ", interest='" + interest + "'" +
            ", maxInterest='" + maxInterest + "'" +
            ", minInterest='" + minInterest + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
