package com.upscale.front.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.upscale.front.FrontendApp;
import com.upscale.front.data.ClientData;
import com.upscale.front.data.Collateral;
import com.upscale.front.data.LoanData;
import com.upscale.front.domain.Loan;
import com.upscale.front.domain.Tenant;
import com.upscale.front.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Inject
    private LoanProductsService loanProductsService;

    @Inject
    private UserService userService;


    @Test
    public void assertCreateClient() throws UnirestException{

        ClientData clientData = new ClientData(1L,"saransh","sharma","1237", "dd MMMM yyyy","en", "true", "21 July 2016", "21 July 2016");
        //mifosBaseServices.createClient(clientData,"https://localhost:8443/fineract-provider/api/v1/clients?tenantIdentifier=default");
    }

    @Test
    public void assertThatCreateLoanAccount() throws UnirestException {

    	User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
    	Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
    	Collateral collateral = new Collateral(13L, "45000", "collateral description here");
    	List<Collateral> collaterals = Arrays.asList(collateral);
    	SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    	LoanData loanData = new LoanData(1L, 1L, "105000", 12, 1, "individual", 12, 1, 1, "15.5", 1, 1, 1, 1,
    			sdf.format(LocalDate.now()).toString(), sdf.format(LocalDate.now()).toString(), "dd MMMM yyyy", "en-GB", collaterals);
    	Loan loan = mifosBaseServices.createLoanAccount(loanData, tenant, user);
    	assertThat(loan.getLoanId()).isNotNull();
    }


    /**
     * Assert that when sending a tenantName it should save the loanProducts in the gateway DB
     * @throws UnirestException
     */

    @Test
    public void assertRetrieveProducts() throws UnirestException{

/*
       // LoanProducts loanProducts = mifosBaseServices.retrieveProduct("https://localhost:8443/fineract-provider/api/v1/loanproducts?tenantIdentifier=default&pretty=true");
        System.out.println("principal " + loanProducts.getPrincipal());
        System.out.println("max Principal " + loanProducts.getMaxPrincipal());

        loanProductsService.save(loanProducts);
        System.out.println("min Principal " + loanProducts.getMinPrincipal());*/

        Tenant tenant = new Tenant();

        tenant.setTenant("default");
        /**
         * Send the tenant Name to Mifos and Get the Status Code and assertThat it is storing or not, this assertion is
         * based on whether the Mifos Service is available or not,
         */
//
//        List<LoanProducts> loanretrieved = mifosBaseServices.retrieveProduct(tenant);
//        assertRetrieveProducts(loanretrieved.);





    }

}
