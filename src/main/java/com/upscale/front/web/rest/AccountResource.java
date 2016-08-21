package com.upscale.front.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.api.client.json.Json;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.mashape.unirest.http.JsonNode;
import com.mysql.fabric.xmlrpc.base.Array;
import com.upscale.front.domain.Documents;
import com.upscale.front.domain.User;
import com.upscale.front.repository.UserRepository;
import com.upscale.front.security.AuthoritiesConstants;
import com.upscale.front.security.SecurityUtils;
import com.upscale.front.service.DocumentService;
import com.upscale.front.service.LoanProductsService;
import com.upscale.front.data.Collateral;
import com.upscale.front.data.LoanData;
import com.upscale.front.data.LoanDetail;
import com.upscale.front.domain.Client;
import com.upscale.front.domain.CollateralData;
import com.upscale.front.domain.Loan;
import com.upscale.front.domain.LoanProducts;
import com.upscale.front.domain.Products;
import com.upscale.front.domain.Tenant;
import com.upscale.front.service.ClientService;
import com.upscale.front.service.CollateralService;
import com.upscale.front.service.LoanService;
import com.upscale.front.service.MailService;
import com.upscale.front.service.MifosBaseServices;
import com.upscale.front.service.ProductsService;
import com.upscale.front.service.SMSService;
import com.upscale.front.service.TextDetection;
import com.upscale.front.service.UserService;
import com.upscale.front.service.TenantService;
import com.upscale.front.service.util.TextExtractionUtil;
import com.upscale.front.web.rest.dto.KeyAndPasswordDTO;
import com.upscale.front.web.rest.dto.ManagedUserDTO;
import com.upscale.front.web.rest.dto.UserDTO;
import com.upscale.front.web.rest.util.HeaderUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.expression.Arrays;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

	private final Logger log = LoggerFactory.getLogger(AccountResource.class);

	private static final int MAX_RESULTS = 2;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserService userService;

	@Inject
	private MailService mailService;

	@Inject
	private DocumentService documentService;

	@Inject
	private SMSService smsService;

	@Inject
	private MifosBaseServices mifosBaseServices;

	@Inject
	private TenantService tenantService;

	@Inject
	private ClientService clientService;

	@Inject
	private LoanService loanService;

	@Inject
	private LoanProductsService loanProductsService;

	@Inject
	private CollateralService collateralService;

	@Inject
	private ProductsService productsService;
	
	/**
	 * POST /register : register the user.
	 *
	 * @param managedUserDTO
	 *            the managed user DTO
	 * @param request
	 *            the HTTP request
	 * @return the ResponseEntity with status 201 (Created) if the user is
	 *         registred or 400 (Bad Request) if the login or e-mail is already
	 *         in use
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.TEXT_PLAIN_VALUE })
	@Timed
	public ResponseEntity<?> registerAccount(@Valid @RequestBody ManagedUserDTO managedUserDTO,
			HttpServletRequest request) {

		HttpHeaders textPlainHeaders = new HttpHeaders();
		textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

		return userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase())
				.map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
				.orElseGet(() -> userRepository.findOneByEmail(managedUserDTO.getEmail())
						.map(user -> new ResponseEntity<>("e-mail address already in use", textPlainHeaders,
								HttpStatus.BAD_REQUEST))
						.orElseGet(() -> {
							User user = userService.createUserInformation(managedUserDTO.getLogin(),
									managedUserDTO.getPassword(), managedUserDTO.getFirstName(),
									managedUserDTO.getLastName(), managedUserDTO.getEmail().toLowerCase(),
									managedUserDTO.getMobile(), managedUserDTO.getLangKey());
							String baseUrl = request.getScheme() + // "http"
									"://" + // "://"
									request.getServerName() + // "myhost"
									":" + // ":"
									request.getServerPort() + // "80"
									request.getContextPath(); // "/myContextPath"
																// or "" if
																// deployed in
																// root context

							mailService.sendActivationEmail(user, baseUrl);
							return new ResponseEntity<>(HttpStatus.CREATED);
						}));
	}

	/**
	 * POST /mobileRegister : register through mobile.
	 *
	 * @param managedUserDTO
	 *            the managed user DTO
	 * @param request
	 *            the HTTP request
	 * @return the ResponseEntity with status 201 (Created) if the user is
	 *         registered or 400 (Bad Request) if the login or mobile is already
	 *         in use
	 *
	 */

	@RequestMapping(value = "/mobileRegister", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	@Timed
	public ResponseEntity<?> mobileRegister(@Valid @RequestBody ManagedUserDTO managedUserDTO,
			HttpServletRequest request) {
		HttpHeaders textHttpHeaders = new HttpHeaders();

		textHttpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return userRepository.findOneByMobile(managedUserDTO.getMobile())
				.map(user -> new ResponseEntity<>("Mobile Already In Use", textHttpHeaders, HttpStatus.BAD_REQUEST))
				.orElseGet(() -> {
					User user = userService.createUserInformation(managedUserDTO.getLogin(),
							managedUserDTO.getPassword(), managedUserDTO.getFirstName(), managedUserDTO.getLastName(),
							managedUserDTO.getEmail(), managedUserDTO.getMobile(), managedUserDTO.getLangKey());
					// Implement SMS Service
					try {
						smsService.SendOtp(user, user.getMobile());

					} catch (IOException e) {
						e.printStackTrace();
					}
					return new ResponseEntity<>(HttpStatus.CREATED);
				});
	}

	/**
	 * GET /activate : activate the registered user.
	 *
	 * @param key
	 *            the activation key
	 * @return the ResponseEntity with status 200 (OK) and the activated user in
	 *         body, or status 500 (Internal Server Error) if the user couldn't
	 *         be activated
	 */
	@RequestMapping(value = "/activate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
		return userService.activateRegistration(key).map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /activatem : activate the mobile registered
	 *
	 * @param code
	 *            the activation code
	 * @return the ResponseEntity with status 200 (OK) and the activated user in
	 *         body or status 500 if the user cannot be activated
	 */

	@RequestMapping(value = "/activatem", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> activateFromMobile(@RequestParam(value = "code") String code,
			@RequestParam(value = "mobile") String mobile) {
		return userService.activateFromMobile(mobile, code).map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /authenticate : check if the user is authenticated, and return its
	 * login.
	 *
	 * @param request
	 *            the HTTP request
	 * @return the login if the user is authenticated
	 */
	@RequestMapping(value = "/authenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account : get the current user.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the current user in
	 *         body, or status 500 (Internal Server Error) if the user couldn't
	 *         be returned
	 */
	@RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserDTO> getAccount() {
		return Optional.ofNullable(userService.getUserWithAuthorities())
				.map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account : update the current user information.
	 *
	 * @param userDTO
	 *            the current user information
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) or 500 (Internal Server Error) if the user couldn't be
	 *         updated
	 */
	@RequestMapping(value = "/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> saveAccount(@Valid @RequestBody UserDTO userDTO) {
		Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use"))
					.body(null);
		}
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
					userDTO.getLangKey());
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/***
	 * POST /account/client : create a client for a tenant in mifos service
	 * 
	 * @param tenant
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the client created successfully
	 */
	@RequestMapping(value = "/account/client", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createClient(@RequestParam(value = "tenant") String tenant) {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {

			Optional<Tenant> tenantData = tenantService.findOneByTenantName(tenant);

			if (!tenantData.isPresent()) {
				return new ResponseEntity<String>("The Tenant Does Not Exist", HttpStatus.NOT_FOUND);
			}
			try {
				Client client = mifosBaseServices.createClient(u, tenantData.get());
				clientService.save(client);
				if(u.getUserImage() != null)
					mifosBaseServices.uploadImage(client, tenantData.get(), u);
				Optional<List<Documents>> document = documentService.findAllByUser(u);
				if (document.isPresent()) {
					mifosBaseServices.uploadDocuments(client, tenantData.get(), document.get());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /documents : upload the current user's image
	 * 
	 * @param file
	 *            image file
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the image not in proper format
	 */
	@RequestMapping(value = "/account/image", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {

		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			try {
				u.setUserImage(file.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
			userRepository.save(u);
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/***
	 * POST /account/loan : create a loan for a tenant in mifos service
	 * 
	 * @param tenant
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the loan created successfully
	 */
	@RequestMapping(value = "/account/loan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createLoan(@RequestBody LoanDetail loanDetail) {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			LoanProducts loanProduct = loanProductsService.findOne(Long.parseLong(loanDetail.getLoanProductId()));
			Products product = productsService.findOne(Long.parseLong(loanDetail.getProductId()));
			Optional<Tenant> tenantData = tenantService.findOneByTenantName(loanProduct.getTenant().getTenant());
			if (!tenantData.isPresent()) {
				return new ResponseEntity<String>("The Tenant Does Not Exist", HttpStatus.NOT_FOUND);
			}
			CollateralData collateralType = collateralService.findCollateralByTenantAndName(tenantData.get(), product.getProductType());
			Optional<Client> client = clientService.findOneByTenantAndUser(tenantData.get(), u);
			if (client.isPresent()) {
				Client clientData = client.get();
				try {
					LoanData loanData = new LoanData();
					loanData.setClientId(clientData.getClientId());
					loanData.setProductId(loanProduct.getMifosProductId());
					loanData.setPrincipal(loanDetail.getPrincipal());
					loanData.setLoanTermFrequency(Integer.parseInt(loanDetail.getTerm()));
					loanData.setNumberOfRepayments(Integer.parseInt(loanDetail.getTerm()));
					loanData.setInterestRatePerPeriod(loanProduct.getInterest().toString());
					DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
					Date date = Calendar.getInstance().getTime();
					loanData.setExpectedDisbursementDate(df.format(Calendar.getInstance().getTime()));
					loanData.setSubmittedOnDate(df.format(Calendar.getInstance().getTime().getTime()));
					loanData.setDateFormat("dd MMMM yyyy");
					loanData.setLocale("en_GB");
					Collateral collateral = new Collateral();
						collateral.setType(collateralType.getMifosCollateralId());
						collateral.setValue(product.getPrice().toString());
						collateral.setDescription(product.getBrand() + "\n" + product.getName()
								+ "\n" + product.getModel());
					List<Collateral> collaterals = new ArrayList<>();
						collaterals.add(collateral);
					loanData.setCollateral(collaterals);
					Loan loan = mifosBaseServices.createLoanAccount(loanData, tenantData.get(), u);
					loanService.save(loan);
				} catch (Exception e) {
					e.printStackTrace();
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /account/loans : get the current logged in user's loans details
	 * 
	 * @return the ResponseEntity with status 200 (OK) and the current user's
	 *         loans details in the body , or status 500 (Internal Server Error)
	 *         if the user's loan details couldn't be returned
	 */
	@RequestMapping(value = "/account/loans", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<JsonNode>> retrieveLoans() {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			Optional<List<Loan>> loans = loanService.findByUser(u);
			if(!loans.isPresent())
				return new ResponseEntity<List<JsonNode>>(HttpStatus.NOT_FOUND);
			List<JsonNode> result = new ArrayList<>();
			for (Loan loan : loans.get()) {
				try {
					result.add(mifosBaseServices.retrieveLoanDetails(loan));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new ResponseEntity<List<JsonNode>>(result, HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /account/documents : get the current logged in user's document
	 * 
	 * @return the ResponseEntity with status 200 (OK) and the current user's
	 *         document in body, or status 500 (Internal Server Error) if the
	 *         user's document couldn't be returned
	 */
	@RequestMapping(value = "/account/documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Object> getDocuments() {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			Optional<List<Documents>> document = documentService.findAllByUser(u);
			if (document.isPresent()) {
				List<JSONObject> entities = new ArrayList<JSONObject>();
				for (Documents n : document.get()) {
					JSONObject entity = new JSONObject();
					entity.put("id", n.getId());
					entity.put("documentType", n.getDocumentType());
					entity.put("documentName", n.getDocumentName());
					entity.put("documentData", StringUtils.split(n.getDocumentData(), "\n"));
					entity.put("documentId", n.getDocumentId());
					entity.put("documentImage", n.getDocumentImage());
					entity.put("contentType", n.getContentType());
					entities.add(entity);
				}
				return new ResponseEntity<Object>(entities, HttpStatus.OK);
			} else
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account/documents : upload the current user's documents
	 * 
	 * @param document
	 *            the document model
	 * @param file
	 *            document file
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the documents not in proper format
	 */
	@RequestMapping(value = "/account/documents", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<String> uploadDocuments(@RequestParam("documentType") String documentType,
			@RequestParam("file") MultipartFile file) throws NullPointerException {

		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {

			Documents document = new Documents();
			Optional<Documents> doc = documentService.findByDocumentTypeAndUser(documentType, u);
			if (doc.isPresent())
				document = doc.get();

			document.setDocumentName(file.getOriginalFilename());
			document.setContentType(file.getContentType());
			document.setDocumentType(documentType);
			try {
				document.setDocumentImage(file.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			/**
			 * Google vision api for extracting data from the document
			 */
			try {
				TextDetection app = new TextDetection(TextDetection.getVisionService());
				List<EntityAnnotation> text = app.detectText(file.getBytes(), MAX_RESULTS);
				System.out.printf("Found %d text%s\n", text.size(), text.size() == 1 ? "" : "s");
				document.setDocumentData(text.get(0).getDescription());
				TextExtractionUtil data = new TextExtractionUtil();

				/**
				 * extract document ID number
				 */
				String result = data.extractDocumentId(text.get(0).getDescription(), document.getDocumentType());
				if (result != null)
					document.setDocumentId(result);
				else
					throw new NullPointerException();
				// u.setAddress(data.extractAddress(text.get(0).getDescription(),
				// document.getDocumentType()));
				// u.setFatherName(data.extractFatherName(text.get(0).getDescription(),
				// document.getDocumentType()));
				Date date = data.extractDOB(text.get(0).getDescription(), document.getDocumentType());
				if (date != null)
					u.setBirthDate(date);
				else
					throw new NullPointerException();
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			userRepository.save(u);
			document.setUser(u);
			documentService.save(document);
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	
	/**
     * DELETE  /account/documents : delete the document by type.
     *
     * @param document type of the document to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/account/documents",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> deleteProducts(@RequestParam("documentType") String documentType ) {
    	return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			Optional<Documents> document = documentService.findByDocumentTypeAndUser(documentType, u);
			if (document.isPresent()) {
					documentService.delete(document.get().getId());
				return new ResponseEntity<String>(HttpStatus.OK);
			}else
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
	/**
	 * POST /account/change_password : changes the current user's password
	 *
	 * @param password
	 *            the new password
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the new password is not strong enough
	 */
	@RequestMapping(value = "/account/change_password", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> changePassword(@RequestBody String password) {
		if (!checkPasswordLength(password)) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		userService.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * POST /account/reset_password/init : Send an e-mail to reset the password
	 * of the user
	 *
	 * @param mail
	 *            the mail of the user
	 * @param request
	 *            the HTTP request
	 * @return the ResponseEntity with status 200 (OK) if the e-mail was sent,
	 *         or status 400 (Bad Request) if the e-mail address is not
	 *         registred
	 */
	@RequestMapping(value = "/account/reset_password/init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
		return userService.requestPasswordReset(mail).map(user -> {
			String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
			mailService.sendPasswordResetMail(user, baseUrl);
			return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
		}).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
	}

	/**
	 * POST /account/reset_password/finish : Finish to reset the password of the
	 * user
	 *
	 * @param keyAndPassword
	 *            the generated key and the new password
	 * @return the ResponseEntity with status 200 (OK) if the password has been
	 *         reset, or status 400 (Bad Request) or 500 (Internal Server Error)
	 *         if the password could not be reset
	 */
	@RequestMapping(value = "/account/reset_password/finish", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
				.map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private boolean checkPasswordLength(String password) {
		return (!StringUtils.isEmpty(password) && password.length() >= ManagedUserDTO.PASSWORD_MIN_LENGTH
				&& password.length() <= ManagedUserDTO.PASSWORD_MAX_LENGTH);
	}

}
