package com.upscale.front.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * Created by saransh on 19/07/16.
 */
@Entity
@Table(name = "jhi_selfService")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "selfService")
public class SelfServiceUser extends AbstractAuditingEntity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "is_selfService", nullable = false)
    private boolean isSelfService;

    @JsonIgnore
    @OneToOne
    @JoinTable(
        name = "jhi_user",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private User user = new User();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsSelfService() { return isSelfService; }

    public void setIsSelfService(boolean isSelfService) { this.isSelfService = isSelfService; }

    public User getUser() { return user; }

    public void getUser(User user) { this.user = user; }



}
