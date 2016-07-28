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
import com.upscale.front.domain.LoanProducts;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by saransh on 20/07/16.
 */
@Service
public class MifosBaseServices extends Unirest {

	Logger log = LoggerFactory.getLogger(MifosBaseServices.class);

	// Getting Values For this method like Url , and Objects or Values which are
	// being sent , like officeId, firstName, lastName, externalId,

	public com.mashape.unirest.http.HttpResponse<JsonNode> createClient(ClientData client, String url)
			throws UnirestException {

		/**
		 * Method which will get the ClienData and Url To Send For and returns
		 * the
		 */

		if (client == null) {
			log.debug(client.toString());
			throw new RuntimeException();
		}

		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

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
		
		SSLContext sslcontext;
		try {
			sslcontext = SSLContexts.custom()
			        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
			        .build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom()
	                         .setSSLSocketFactory(sslsf)
	                         .build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		System.out.println(client);

		HttpResponse<JsonNode> post = Unirest.post(url).header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic bWlmb3M6cGFzc3dvcmQ=")
				.body(client).asJson();

		log.debug("String", post.getStatus());

		System.out.println();
		System.out.println(post.getBody());
		log.debug("String ", post);
		JSONObject obj = post.getBody().getObject();
		
		System.out.println("Client ID is" + obj.getInt("clientId"));
		return post;
	}
	
	
	public com.mashape.unirest.http.HttpResponse<JsonNode> retrieveProduct(String url)
			throws UnirestException {

		/**
		 * Method which will get the loan product data
		 */

		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

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
		
		SSLContext sslcontext;
		try {
			sslcontext = SSLContexts.custom()
			        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
			        .build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom()
	                         .setSSLSocketFactory(sslsf)
	                         .build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LoanProducts loanProducts = new LoanProducts();
		HttpResponse<JsonNode> result = Unirest.get(url).header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic bWlmb3M6cGFzc3dvcmQ=")
				.asJson();

		log.debug("String", result.getStatus());

		System.out.println();
		System.out.println(result.getBody());
		log.debug("String ", result);
		JSONObject obj = result.getBody().getObject();
		loanProducts.setPrincipal(new BigDecimal(obj.getLong("principal")));
		//System.out.println("Client ID is" + obj.getInt("clientId"));
		return result;
	}

}
