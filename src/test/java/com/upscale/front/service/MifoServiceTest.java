package com.upscale.front.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.upscale.front.FrontendApp;
import com.upscale.front.data.ClientData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Created by saransh on 21/07/16.
 * Test For Mifos Services API
 *
 * @see MifosBaseServices
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FrontendApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class MifoServiceTest {

    @Inject
    private MifosBaseServices mifosBaseServices;


    @Test
    public void assertCreateClient() throws UnirestException{

        ClientData clientData = new ClientData(1L,"saransh","sharma","1236", "dd MMMM yyyy","en", "true", "21 July 2016", "21 July 2016");



        mifosBaseServices.createClient(clientData,"https://localhost:8443/fineract-provider/api/v1/clients?tenantIdentifier=default");



    }
    
    @Test
    public void assertRetrieveProducts() throws UnirestException{

        mifosBaseServices.retrieveProduct("https://localhost:8443/fineract-provider/api/v1/loanproducts?tenantIdentifier=default");



    }
}
