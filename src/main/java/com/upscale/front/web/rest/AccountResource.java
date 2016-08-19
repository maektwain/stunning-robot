package com.upscale.front.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.upscale.front.data.LoanData;
import com.upscale.front.domain.*;
import com.upscale.front.repository.UserRepository;
import com.upscale.front.security.SecurityUtils;
import com.upscale.front.service.*;
import com.upscale.front.service.util.TextExtractionUtil;
import com.upscale.front.web.rest.dto.KeyAndPasswordDTO;
import com.upscale.front.web.rest.dto.ManagedUserDTO;
import com.upscale.front.web.rest.dto.UserDTO;
import com.upscale.front.web.rest.util.HeaderUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
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
	@RequestMapping(value = "/account/client",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createClient(@RequestParam(value = "tenant") String tenant){
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {

			Optional<Tenant> tenantData = tenantService.findOneByTenantName(tenant);

			if (tenantData ==  null){
	            return new ResponseEntity<String>("The Tenant Does Not Exist", HttpStatus.NOT_FOUND);
	        }
			try {
				Client client = mifosBaseServices.createClient(u, tenantData.get());
				clientService.save(client);
                mifosBaseServices.uploadImage(client, tenantData.get(), u);
				//Client client =clientService.findOneByTenantAndUser(tenantData.get(), u);
				mifosBaseServices.uploadDocuments(client, tenantData.get(), u);

			} catch (Exception e) {
				System.out.println("Client Creation Failed");
				e.printStackTrace();
			}

			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}


	/**
	 * POST /documents : upload the current user's image
	 *
	 * @param file
	 * 			image file
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
	@RequestMapping(value = "/account/loan",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createLoan(@RequestParam(value = "tenant") String tenant,
			@RequestBody LoanData loanData){
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {

			Optional<Tenant> tenantData = tenantService.findOneByTenantName(tenant);

			if (tenantData ==  null){
	            return new ResponseEntity<String>("The Tenant Does Not Exist", HttpStatus.NOT_FOUND);
	        }

			Client clientData = clientService.findOneByTenantAndUser(tenantData.get(), u);
			if(clientData != null){
				try {
					loanData.setClientId(clientData.getClientId());
					Loan loan = mifosBaseServices.createLoanAccount(loanData, tenantData.get(), u);
					loanService.save(loan);
				} catch (Exception e) {
					System.out.println("Loan Creation Failed");
					e.printStackTrace();
				}
			}
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}
	/**
	 * GET /documents : get the current logged in user's document
	 *
	 * @return the ResponseEntity with status 200 (OK) and the current user's document in
	 *         body, or status 500 (Internal Server Error) if the user's document couldn't
	 *         be returned
	 */
	@RequestMapping(value = "/documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Object> getDocuments() {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			List<Documents> doc = documentService.findAllByUser(u);
			List<JSONObject> entities = new ArrayList<JSONObject>();

			for (Documents n : doc) {
				JSONObject entity = new JSONObject();
				entity.put("id", n.getId());
				entity.put("documentType", n.getDocumentType());
				entity.put("documentName", n.getDocumentName());
				entity.put("documentData", StringUtils.split(n.getDocumentData(),"\n"));
				entity.put("documentId", n.getDocumentId());
				entity.put("contentType", n.getContentType());
                entity.put("documentImage", n.getDocumentImage());
				entities.add(entity);
			}
			return new ResponseEntity<Object>(entities, HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /documents : upload the current user's documents
	 *
	 * @param document
	 * 			the document model
	 * @param file
	 * 			document file
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the documents not in proper format
	 */
	@RequestMapping(value = "/documents", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<String> uploadDocuments(@ModelAttribute("documents") Documents document,
			@RequestParam("file") MultipartFile file) {

		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			document.setDocumentName(file.getOriginalFilename());
			document.setContentType(file.getContentType());
			document.setUser(u);
			try {
				document.setDocumentImage(file.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {

				TextDetection app = new TextDetection(TextDetection.getVisionService());
				List<EntityAnnotation> text = app.detectText(file.getBytes(), MAX_RESULTS);
				System.out.printf("Found %d text%s\n", text.size(), text.size() == 1 ? "" : "s");
				document.setDocumentData(text.get(0).getDescription());
				TextExtractionUtil data = new TextExtractionUtil();
				String result = data.extractDocumentId(text.get(0).getDescription(), document.getDocumentType());

				if(result.equals("documeny type not found"))
					document.setDocumentId(null);
				else
					document.setDocumentId(result);
				//u.setAddress(data.extractAddress(text.get(0).getDescription(), document.getDocumentType()));
				//u.setFatherName(data.extractFatherName(text.get(0).getDescription(), document.getDocumentType()));
				//u.setBirthDate(data.extractDOB(text.get(0).getDescription(), document.getDocumentType()));
			} catch (Exception e) {
				System.out.println("Google vision api credential error");
				e.printStackTrace();
			}

			userRepository.save(u);
			document.setUser(u);
			documentService.save(document);

			return new ResponseEntity<String>(HttpStatus.OK);
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
