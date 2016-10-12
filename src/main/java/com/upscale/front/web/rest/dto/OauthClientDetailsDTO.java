package com.upscale.front.web.rest.dto;

import com.upscale.front.domain.OauthClientDetails;

import javax.validation.constraints.Size;

/**
 * Created by saransh on 30/09/16.
 */
public class OauthClientDetailsDTO {


    @Size(min = 1, max = 50)
    private String applicationname;


    @Size(max = 200)
    private String applicationdescription;


    @Size(max = 50)
    private String callbackurl;


    public OauthClientDetailsDTO() {

    }

    public OauthClientDetailsDTO(OauthClientDetails oauthClientDetails){
        this(oauthClientDetails.getApplicationname(),
            oauthClientDetails.getAdditionalinformation(),oauthClientDetails.getWebserverredirecturi());
    }

    public OauthClientDetailsDTO(String applicationname, String applicationdescription,String callbackurl){
        this.applicationname = applicationname;
        this.applicationdescription = applicationdescription;
        this.callbackurl = callbackurl;

    }


    public String getApplicationname() {return applicationname;}

    public String getApplicationdescription() { return applicationdescription;}

    public String getCallbackurl() { return callbackurl;}


    @Override
    public String toString(){
        return "OauthClientDetailsDTO{" +
            "applicationname= '" + applicationname + '\'' +
            ", applicationdescription= '" +  applicationdescription + '\'' +
            ", callbackurl= '" + callbackurl + '\'' +
            "}";
    }

}
