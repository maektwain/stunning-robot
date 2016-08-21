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
import com.upscale.front.domain.Client;
import com.upscale.front.domain.Collateral;
import com.upscale.front.domain.Documents;
import com.upscale.front.data.LoanData;
import com.upscale.front.domain.*;
import com.upscale.front.repository.DocumentsRepository;
import com.upscale.front.repository.TenantsRepository;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by saransh on 20/07/16.
 *
 * Updated by Anurag
 */
@Service
@Transactional
public class MifosBaseServices extends Unirest {

	Logger log = LoggerFactory.getLogger(MifosBaseServices.class);

	private static final String URL = "https://localhost:8443/fineract-provider/api/v1";

	@Inject
	private TenantsRepository tenantsRepository;

	@Inject
	private DocumentsRepository documentsRepository;

	// Getting Values For this method like Url , and Objects or Values which are
	// being sent , like officeId, firstName, lastName, externalId,

	public Client createClient(User user, Tenant tenant) throws UnirestException {

		/**
		 * Method which will get the User and Tenant Data To Send For client Creation
		 * and returns the client id from mifos service
		 */

		ClientData client = new ClientData();
		client.setFirstname(user.getFirstName());
		client.setLastname(user.getLastName());
		client.setDateFormat("dd MMMM yyyy");
		client.setLocale("en");
		client.setActive("true");
		client.setOfficeId(1L);
		client.setExternalId(null);
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		Date date = new Date();
		client.setSubmittedOnDate(dateFormat.format(date));
		client.setActivationDate(dateFormat.format(date));

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
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}


		HttpResponse<JsonNode> post = Unirest.post(URL + "/clients?tenantIdentifier=" + tenant.getTenant())
				.header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic " + tenant.getAuthKey())
				.body(client).asJson();

		log.debug("String", post.getStatus());
		log.debug("String ", post);
		JSONObject obj = post.getBody().getObject();
		Client result = new Client();
		result.setClientId(obj.getLong("clientId"));
		result.setTenant(tenant);
		result.setUser(user);
		return result;
	}

	public List<LoanProducts> retrieveProduct(Tenant tenant) throws UnirestException {

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

			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		List<LoanProducts> loanProductList = new ArrayList<LoanProducts>();
		HttpResponse<JsonNode> result = Unirest.get(URL + "/loanproducts?tenantIdentifier=" + tenant.getTenant())
				.header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic " + tenant.getAuthKey())
				.asJson();

		log.debug("String", result.getStatus());
		log.debug("String ", result);
		JSONArray obj = result.getBody().getArray();
		for (int i = 0; i < obj.length(); i++) {
			LoanProducts loanProducts = new LoanProducts();
			JSONObject res = obj.getJSONObject(i);
			loanProducts.setName(res.getString("name"));
			loanProducts.setMifosProductId(res.getLong("id"));
			loanProducts.setTenant(tenant);
			loanProducts.setPrincipal(new BigDecimal(res.getLong("principal")));
			loanProducts.setMaxPrincipal(new BigDecimal(res.getLong("maxPrincipal")));
			loanProducts.setMinPrincipal(new BigDecimal(res.getLong("minPrincipal")));
			loanProducts.setDownpayment(new BigDecimal(res.getLong("downpayment")));
			loanProducts.setMaxDownpayment(new BigDecimal(res.getLong("maxDownpayment")));
			loanProducts.setMinDownpayment(new BigDecimal(res.getLong("minDownpayment")));
			loanProducts.setTenure(res.getInt("numberOfRepayments"));
			loanProducts.setMinTenure(res.getInt("minNumberOfRepayments"));
			loanProducts.setMaxTenure(res.getInt("maxNumberOfRepayments"));
			loanProducts.setInterest(new BigDecimal(res.getLong("interestRatePerPeriod")));
			loanProducts.setMinInterest(new BigDecimal(res.getLong("minInterestRatePerPeriod")));
			loanProducts.setMaxInterest(new BigDecimal(res.getLong("maxInterestRatePerPeriod")));
			loanProducts.setDescription(res.getString("description"));
			loanProductList.add(loanProducts);
		}
		return loanProductList;
	}

	public List<Collateral> retrieveCollateralList(Tenant tenant) throws UnirestException {

		/**
		 * Method which will get the collateral types with code values
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

			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		List<Collateral> collateralList = new ArrayList<Collateral>();
		HttpResponse<JsonNode> result = Unirest.get(URL + "/codes/2/codevalues?tenantIdentifier=" + tenant.getTenant())
				.header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic " + tenant.getAuthKey())
				.asJson();

		log.debug("String", result.getStatus());
		log.debug("String ", result);
		JSONArray obj = result.getBody().getArray();
		for (int i = 0; i < obj.length(); i++) {
			Collateral collateral = new Collateral();
			JSONObject res = obj.getJSONObject(i);
			collateral.setName(res.getString("name"));
			collateral.setTenant(tenant);
			collateral.setMifosCollateralId(res.getLong("id"));
			collateral.setActive(res.getBoolean("isActive"));
			collateral.setDescription(res.getString("description"));
			collateralList.add(collateral);
		}
		return collateralList;
	}
	
	
	public JsonNode retrieveLoanDetails(Loan loan) throws UnirestException {

		/**
		 * Method which will get the loan details of a user
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

			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		HttpResponse<JsonNode> result = Unirest.get(URL + "/loans/" + loan.getLoanId() + "?tenantIdentifier=" + loan.getTenant().getTenant())
				.header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic " + loan.getTenant().getAuthKey())
				.asJson();

		log.debug("String", result.getStatus());
		log.debug("String ", result);
		JsonNode obj = result.getBody();
		return obj;
	}

	public Loan createLoanAccount(LoanData loan, Tenant tenant, User user) throws UnirestException {

		/**
		 * Method which will get the Loan Data along with tenant and user To Send For and returns
		 * the loan Id
		 */

		if (loan == null) {
			log.debug(loan.toString());
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
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		HttpResponse<JsonNode> post = Unirest.post(URL + "/loans?tenantIdentifier=" + tenant.getTenant())
				.header("accept", "application/json")
				.header("Content-Type", "application/json").header("Authorization", "Basic " + tenant.getAuthKey())
				.body(loan).asJson();

		log.debug("String", post.getStatus());
		log.debug("String ", post);
		JSONObject obj = post.getBody().getObject();
		Loan result = new Loan();
		result.setLoanId(obj.getLong("loanId"));
		result.setTenant(tenant);
		result.setUser(user);
		return result;
	}

	public HttpResponse<String> uploadImage(Client client, Tenant tenant, User user) throws UnirestException, URISyntaxException, IOException {
		/**
		 * Method which will get the Client Data along with tenant and user to upload image
		 * and returns the status
         *
		 */

		if (user.getUserImage() == null) {
			log.debug(user.toString());
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
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		InputStream in = new ByteArrayInputStream(user.getUserImage());
		BufferedImage image = ImageIO.read(in);
		String fileName = LocalDateTime.now().toString().replace(":", "") + ".jpg";
		ImageIO.write(image, "jpg", new File("D:\\" + fileName));
		HttpResponse<String> post = Unirest.post(URL + "/clients/" + client.getClientId() + "/images?tenantIdentifier=" + tenant.getTenant())
				.header("accept", "application/json")
				.header("Authorization", "Basic " + tenant.getAuthKey())
				.field("file", new File("D:\\" + fileName), "image/jpeg")
				.asString();

			log.debug("String", post.getStatus());
			log.debug("String ", post);
			return post;
	}
	
	public ArrayList<Integer> uploadDocuments(Client client, Tenant tenant, List<Documents> documents) throws UnirestException, URISyntaxException, IOException {	
		/**
		 * Method which will get the Client Data along with tenant and user to upload documents
		 * and returns the status
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
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		ArrayList<Integer> status = new ArrayList<>();

		for(Documents doc: documents){
			InputStream in = new ByteArrayInputStream(doc.getDocumentImage());
			BufferedImage image = ImageIO.read(in);
			String fileName = LocalDateTime.now().toString().replace(":", "") + ".jpg";
			ImageIO.write(image, "jpg", new File("D:\\" + fileName));
			HttpResponse<String> post = Unirest.post(URL + "/clients/" + client.getClientId() + "/documents?tenantIdentifier=" + tenant.getTenant())
				.header("accept", "application/json")
				.header("Authorization", "Basic " + tenant.getAuthKey())
				.field("description", doc.getDocumentData())
				.field("name", doc.getDocumentName())
				.field("file", new File("D:\\" + fileName ), "image/jpeg")
				.asString();
			log.debug("String", post.getStatus());
			log.debug("String ", post);
			status.add(post.getStatus());
		}

		return status;
	}
}
