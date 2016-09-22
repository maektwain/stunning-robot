package com.upscale.front.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.upscale.front.FrontendApp;
import com.upscale.front.config.Constants;
import com.upscale.front.data.ClientData;
import com.upscale.front.data.Collateral;
import com.upscale.front.data.LoanData;
import com.upscale.front.domain.*;

import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

    @Inject
    private LoanProductsService loanProductsService;

    @Inject
    private UserService userService;


    @Test
    public void assertCreateClient() throws UnirestException{

        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
        Client client = mifosBaseServices.createClient(user, tenant);
        assertThat(client.getClientId()).isNotNull();
    }

    @Test
    public void assertThatCreateLoanAccount() throws UnirestException {

    	User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
    	Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
    	Collateral collateral = new Collateral(13L, "45000", "collateral description here");
    	List<Collateral> collaterals = Arrays.asList(collateral);
    	LoanData loanData = new LoanData(1L, 1L, "105000", 12, 1, "individual", 12, 1, 1, "15.5", 1, 1, 1, 1,
    			"15 Sep 2016", "15 Sep 2016" , "dd MMMM yyyy", "en_GB", collaterals);
    	Loan loan = mifosBaseServices.createLoanAccount(loanData, tenant, user);
    	assertThat(loan.getLoanId()).isNotNull();
    }

    @Test
    public void assertThatDocumentsUploaded() throws UnirestException, IOException, URISyntaxException {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
        Client client = new Client(tenant, user, 1L);
        File file = new File(Constants.FILE_TEMP_DOWNLOAD + "pancard.jpg");
        byte[] image = Files.readAllBytes(file.toPath());
        Documents document = new Documents("PANCARD","testpancard","BSDUF23456", "dummy data for pan card", image);
        List<Documents> documents = Arrays.asList(document);
        ArrayList<Integer> status = mifosBaseServices.uploadDocuments(client,tenant,documents);
        assertThat(status.get(0)).isEqualTo(200);
    }

    @Test
    public void assertThatProfileImageUpload() throws UnirestException, IOException, URISyntaxException {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        File file = new File(Constants.FILE_TEMP_DOWNLOAD + "testimage.jpg");
        byte[] image = Files.readAllBytes(file.toPath());
        user.setUserImage(image);
        Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
        Client client = new Client(tenant, user, 1L);
        HttpResponse<String> status = mifosBaseServices.uploadImage(client,tenant,user);
        assertThat(status.getStatus()).isEqualTo(200);

    }

    @Test
    public void assertThatLoanProductsAreRetrieved() throws UnirestException{
        Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
        List<LoanProducts> loanProducts = mifosBaseServices.retrieveProduct(tenant);
        assertThat(loanProducts).isNotNull();
    }

    @Test
    public void assertThatCollateralsAreRetrieved() throws UnirestException{
        Tenant tenant = new Tenant(1L, "default", "mifos", "password", "bWlmb3M6cGFzc3dvcmQ=");
        List<CollateralData> collaterals = mifosBaseServices.retrieveCollateralList(tenant);
        assertThat(collaterals).isNotNull();
    }


}
