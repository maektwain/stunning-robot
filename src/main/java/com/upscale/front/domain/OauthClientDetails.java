package com.upscale.front.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by saransh on 30/09/16.
 */

@Entity
@Table(name = "oauth_client_details")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "oauthclient")
public class OauthClientDetails extends AbstractAuditingEntity implements Serializable{

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 1, max = 255)
    @Column(name = "client_id"  ,length = 255, unique = true, nullable = false)
    private String applicationname;


    @Column(name = "resource_ids", length = 255)
    private String resourceIds;

    @Column(name = "client_secret", length = 255)
    private String clientsecret;

    @Column(name = "scope", length = 255)
    private String  scopes;

    @Column(name = "authorized_grant_types", length = 255)
    private String  authorizedGrantTypes;

    @Column(name = "web_server_redirect_uri", length = 255)
    private String webserverredirecturi;

    @Column(name = "authorities", length = 255)
    private String  authorities;

    @Column(name = "access_token_validity", length = 11)
    private Integer accesstokenvalidity;

    @Column(name = "refresh_token_validity", length = 11)
    private Integer refreshtokenvalidity;

    @Column(name = "additional_information", length = 4096)
    private String additionalinformation;

    @Column(name = "autoapprove", length = 4096)
    private String autoapprove;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationname() {
        return applicationname;
    }

    public void setApplicationname(String applicationname) {
        this.applicationname = applicationname;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getClientsecret() {
        return clientsecret;
    }

    public void setClientsecret(String clientsecret) {
        this.clientsecret = clientsecret;
    }

    public String getScope() {
        return scopes;
    }

    public void setScope(String... scopes) {
        String joined = String.join(",",scopes);
       this.scopes = joined;
    }

    public String getAuthorizedgranttypes() {
        return authorizedGrantTypes;
    }

    public void  setAuthorizedGrantTypes(String... authorizedGrantTypes) {
        String joined = String.join(",",authorizedGrantTypes);
        this.authorizedGrantTypes = joined;

    }

    public String getWebserverredirecturi() {
        return webserverredirecturi;
    }

    public void setWebserverredirecturi(String webserverredirecturi) {
        this.webserverredirecturi = webserverredirecturi;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String... authorities) {
        String joined = String.join(",", authorities);
        this.authorities = joined;

    }

    public Integer getAccesstokenvalidity() {
        return accesstokenvalidity;
    }

    public void setAccesstokenvalidity(Integer accesstokenvalidity) {
        this.accesstokenvalidity = accesstokenvalidity;
    }

    public Integer getRefreshtokenvalidity() {
        return refreshtokenvalidity;
    }

    public void setRefreshtokenvalidity(Integer refreshtokenvalidity) {
        this.refreshtokenvalidity = refreshtokenvalidity;
    }

    public String getAdditionalinformation() {
        return additionalinformation;
    }

    public void setAdditionalinformation(String additionalinformation) {
        this.additionalinformation = additionalinformation;
    }

    public String getAutoapprove() {
        return autoapprove;
    }

    public void setAutoapprove(String autoapprove) {
        this.autoapprove = autoapprove;
    }

    public User getUser() { return  user; }

    public  void setUser(User user) { this.user = user; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OauthClientDetails that = (OauthClientDetails) o;

        if (applicationname != null ? !applicationname.equals(that.applicationname) : that.applicationname != null)
            return false;
        if (resourceIds != null ? !resourceIds.equals(that.resourceIds) : that.resourceIds != null) return false;
        if (clientsecret != null ? !clientsecret.equals(that.clientsecret) : that.clientsecret != null) return false;
        if (scopes != null ? !scopes.equals(that.scopes) : that.scopes != null) return false;
        if (authorizedGrantTypes != null ? !authorizedGrantTypes.equals(that.authorizedGrantTypes) : that.authorizedGrantTypes != null)
            return false;
        if (webserverredirecturi != null ? !webserverredirecturi.equals(that.webserverredirecturi) : that.webserverredirecturi != null)
            return false;
        if (authorities != null ? !authorities.equals(that.authorities) : that.authorities != null) return false;
        if (accesstokenvalidity != null ? !accesstokenvalidity.equals(that.accesstokenvalidity) : that.accesstokenvalidity != null)
            return false;
        if (refreshtokenvalidity != null ? !refreshtokenvalidity.equals(that.refreshtokenvalidity) : that.refreshtokenvalidity != null)
            return false;
        if (additionalinformation != null ? !additionalinformation.equals(that.additionalinformation) : that.additionalinformation != null)
            return false;
        return autoapprove != null ? autoapprove.equals(that.autoapprove) : that.autoapprove == null;

    }

    @Override
    public int hashCode() {
        int result = applicationname != null ? applicationname.hashCode() : 0;
        result = 31 * result + (resourceIds != null ? resourceIds.hashCode() : 0);
        result = 31 * result + (clientsecret != null ? clientsecret.hashCode() : 0);
        result = 31 * result + (scopes != null ? scopes.hashCode() : 0);
        result = 31 * result + (authorizedGrantTypes != null ? authorizedGrantTypes.hashCode() : 0);
        result = 31 * result + (webserverredirecturi != null ? webserverredirecturi.hashCode() : 0);
        result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
        result = 31 * result + (accesstokenvalidity != null ? accesstokenvalidity.hashCode() : 0);
        result = 31 * result + (refreshtokenvalidity != null ? refreshtokenvalidity.hashCode() : 0);
        result = 31 * result + (additionalinformation != null ? additionalinformation.hashCode() : 0);
        result = 31 * result + (autoapprove != null ? autoapprove.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
