package com.upscale.front.service;

import com.nexmo.messaging.sdk.NexmoSmsClient;
import com.nexmo.messaging.sdk.SmsSubmissionResult;
import com.nexmo.messaging.sdk.messages.TextMessage;
import com.nexmo.verify.sdk.CheckResult;
import com.nexmo.verify.sdk.NexmoVerifyClient;
import com.nexmo.verify.sdk.VerifyResult;
import com.upscale.front.domain.User;
import com.upscale.front.repository.UserRepository;
import com.upscale.front.repository.search.UserSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by saransh on 18/07/16.
 */



@Service
public class SMSService {


    private final Logger log = LoggerFactory.getLogger(SMSService.class);


    @Inject
    private UserRepository userRepository;

    @Inject
    private UserSearchRepository userSearchRepository;


    @Deprecated
    public void SendSmS(String mobile, String code){

        log.debug("Sending SMS as OTP");

          final String API_KEY = "a66c7b32";

          final String API_SECRET = "7039ebc5";

          final String SMS_FROM = "UPSCALE";


        NexmoSmsClient client;

        try{
            client = new NexmoSmsClient(API_KEY, API_SECRET);

        }catch (Exception e){
            System.err.println("failed to insatnciate a Nexmo Client");
            e.printStackTrace();
            throw new RuntimeException("Failed to instanciate a new client");
        }

        TextMessage message = new TextMessage(SMS_FROM, "91" + mobile, code);

        SmsSubmissionResult[] results;

        try {
            results = client.submitMessage(message);

        }catch (Exception e){
            System.err.println("Failed to communicate with nexmo client");
            e.printStackTrace();
            throw new RuntimeException("Failed to communicate with nexmo client");
        }

        System.out.println("... Message submitted in [ " + results.length + " ] parts");
        for (int i=0;i<results.length;i++) {
            System.out.println("--------- part [ " + (i + 1) + " ] ------------");
            System.out.println("Status [ " + results[i].getStatus() + " ] ...");
            if (results[i].getStatus() == SmsSubmissionResult.STATUS_OK)
                System.out.println("SUCCESS");
            else if (results[i].getTemporaryError())
                System.out.println("TEMPORARY FAILURE - PLEASE RETRY");
            else
                System.out.println("SUBMISSION FAILED!");
            System.out.println("Message-Id [ " + results[i].getMessageId() + " ] ...");
            System.out.println("Error-Text [ " + results[i].getErrorText() + " ] ...");

            if (results[i].getMessagePrice() != null)
                System.out.println("Message-Price [ " + results[i].getMessagePrice() + " ] ...");
            if (results[i].getRemainingBalance() != null)
                System.out.println("Remaining-Balance [ " + results[i].getRemainingBalance() + " ] ...");
        }

    }


    public void SendOtp(User user, String mobile) throws IOException {

        log.debug("Sending OTP To Customer");


        final String API_KEY = "a66c7b32";

        final String API_SECRET = "7039ebc5";

        final String SMS_FROM = "UPSCALE";

        NexmoVerifyClient nexmoVerifyClient = null;

        try {

            nexmoVerifyClient = new NexmoVerifyClient(API_KEY,API_SECRET);

        }catch (ParserConfigurationException e){

            e.printStackTrace();;
        }

        VerifyResult verify;

        try {

            verify = nexmoVerifyClient.verify("91" + mobile, SMS_FROM);

        } catch (SAXException e){
            e.printStackTrace();

            throw new NullPointerException();

        }

        System.out.print(verify.toString());



        System.out.print(verify.getRequestId());


        user.setRequestId(verify.getRequestId());

        userRepository.save(user);

        userSearchRepository.save(user);






    }


    public void verifyOTP(String requestId, String code) throws IOException{

        log.debug("Verifying OTP Of Customer");


        final String API_KEY = "a66c7b32";

        final String API_SECRET = "7039ebc5";

        final String SMS_FROM = "UPSCALE";

        NexmoVerifyClient nexmoVerifyClient = null;

        try {

            nexmoVerifyClient = new NexmoVerifyClient(API_KEY,API_SECRET);

        }catch (ParserConfigurationException e){

            e.printStackTrace();;
        }

        CheckResult checkResult;

        try {

            checkResult = nexmoVerifyClient.check(requestId,code);

        } catch (SAXException e){
            e.printStackTrace();

            throw new NullPointerException();

        }



    }






}
