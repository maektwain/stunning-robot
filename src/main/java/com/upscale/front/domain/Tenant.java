package com.upscale.front.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Anurag Garg
 *
 */

@Entity
@Table(name = "jhi_tenant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="tenant")
public class Tenant extends AbstractAuditingEntity implements Serializable{


	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	@Column(name = "tenant_name")
	private String tenantName;

    @com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "user_name")
	private String userName;

    @com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "password")
	private String password;

    @com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "auth_key")
	private String authKey;

    public Tenant(){}

	public Tenant(Long id, String tenantName, String userName, String password, String authKey) {
		super();
		this.id = id;
		this.tenantName = tenantName;
		this.userName = userName;
		this.password = password;
		this.authKey = authKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTenant() {
		return tenantName;
	}

	public void setTenant(String tenant) {
		this.tenantName = tenant;
	}


    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tenant tenant = (Tenant) o;
        if(tenant.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tenant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + id +
            ", tenant='" + tenantName + "'" +
            ", username='" + userName + "'" +
            ", password='" + password + "'" +
            ", authkey='" + authKey + "'" +
            '}';
    }
}
