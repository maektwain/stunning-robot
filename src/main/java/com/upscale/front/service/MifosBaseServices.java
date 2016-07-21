package com.upscale.front.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.upscale.front.data.ClientData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by saransh on 20/07/16.
 */
@Service
public class MifosBaseServices extends Unirest{


    Logger log = LoggerFactory.getLogger(MifosBaseServices.class);


    //Getting Values For this method like Url , and Objects or Values which are being sent , like   officeId, firstName, lastName, externalId,



    public com.mashape.unirest.http.HttpResponse<JsonNode> createClient(ClientData client, String url) throws UnirestException {

        /**
         * Method which will get the ClienData and Url To Send For and returns the
         */

        if (client == null) {
            log.debug(client.toString());
            throw new RuntimeException();
        }

        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                = new com.fasterxml.jackson.databind.ObjectMapper();



            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {

                    jacksonObjectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    return jacksonObjectMapper.writeValueAsString(value);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }



        });

        System.out.println(client);


        HttpResponse<JsonNode> post = Unirest.post(url)
            .header("accept","application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic bWlmb3M6cGFzc3dvcmQ=")
            .body(client)
            .asJson();

        log.debug("String",post.getStatus());

        System.out.println();
        System.out.println(post.getBody());
        log.debug("String ", post);
        return post;



    }











}



