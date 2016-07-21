package com.upscale.front.service;

import com.google.gson.Gson;
import com.upscale.front.domain.Authority;
import com.upscale.front.domain.User;
import com.upscale.front.repository.AuthorityRepository;
import com.upscale.front.repository.UserRepository;
import com.upscale.front.repository.search.UserSearchRepository;
import com.upscale.front.security.AuthoritiesConstants;
import com.upscale.front.security.SecurityUtils;
import com.upscale.front.service.util.RandomUtil;
import com.upscale.front.web.rest.dto.ManagedUserDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserSearchRepository userSearchRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private SMSService smsService;
	
	

	public Optional<User> activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		return userRepository.findOneByActivationKey(key).map(user -> {
			// activate given user for the registration key.
			user.setActivated(true);
			user.setActivationKey(null);
			userRepository.save(user);
			userSearchRepository.save(user);
			log.debug("Activated user: {}", user);
			return user;
		});
	}

	public Optional<User> activateFromMobile(String mobile, String code) {
		log.debug("Activating user for code {}", code);
		return userRepository.findOneByMobile(mobile).map(user -> {
			// activate the given user for mobile number.
			String requestId = user.getRequestId();

			// First Verify Through Nexmo

			try {
				smsService.verifyOTP(requestId, code);
			} catch (IOException e) {
				e.printStackTrace();
			}

			user.setActivated(true);
			user.setActivationKey(null);

			/**
			 * First mifos authentication will happen through an interface and
			 * then get the mifos base key and create a mifos
			 */

			userRepository.save(user);
			userSearchRepository.save(user);

			log.debug("Activating User Through OTP", user);

			// Creating a self service user

			createSelfService(user.getLogin(), user.getPassword());

			return user;
		});
	}

	public void createSelfService(String u, String p) {

		try {
			
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub

				}
			} };

			try {
				SSLContext sc = SSLContext.getInstance("SSL");

				sc.init(null, trustAllCerts, new java.security.SecureRandom());

				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			HostnameVerifier allHostsValid = new HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return false;
				}
	        };
	        
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			URL authurl = new URL("https://localhost:8443/fineract-provider/api/v1/users?tenantIdentifier=default");

			HttpURLConnection connection = (HttpURLConnection) authurl.openConnection();

			String basicAuth = "Basic " + "bWlmb3M6cGFzc3dvcmQ=";
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", basicAuth);
			
			JSONObject jsonparam = new JSONObject();
			ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(1);
     
			
			jsonparam.put("username", "johndoe");
			jsonparam.put("firstname", "john");
			jsonparam.put("lastname", "doe");
			jsonparam.put("email", "john@doe.com");
			jsonparam.put("officeId", new Integer(1));
			jsonparam.put("roles", list);
			jsonparam.put("sendPasswordToEmail", new Boolean(false));
			jsonparam.put("password", "password");
			jsonparam.put("repeatPassword", "password");
			jsonparam.put("isSelfServiceUser", new Boolean(true));

			System.out.println(jsonparam.toString());

			OutputStream os  = connection.getOutputStream();
			os.write(jsonparam.toString().getBytes("UTF-8"));
			os.flush();
			os.close();
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String output;
			
			StringBuffer response = new StringBuffer();
			
			System.out.println("Output From Server....\n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				response.append(output);
			}

			User user = new User();
			JSONObject json = new JSONObject(output);
			
			user.setSelfServiceId(Long.parseLong(json.getString("resourceId")));
			
			connection.disconnect();
			
			

		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		} catch (JSONException j) {
			j.printStackTrace();
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		

	}

	public Optional<User> completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);

		return userRepository.findOneByResetKey(key).filter(user -> {
			ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
			return user.getResetDate().isAfter(oneDayAgo);
		}).map(user -> {
			user.setPassword(passwordEncoder.encode(newPassword));
			user.setResetKey(null);
			user.setResetDate(null);
			userRepository.save(user);
			return user;
		});
	}

	public Optional<User> requestPasswordReset(String mail) {
		return userRepository.findOneByEmail(mail).filter(User::getActivated).map(user -> {
			user.setResetKey(RandomUtil.generateResetKey());
			user.setResetDate(ZonedDateTime.now());
			userRepository.save(user);
			return user;
		});
	}

	public User createUserInformation(String login, String password, String firstName, String lastName, String email,
			String mobile, String langKey) {

		User newUser = new User();
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
		Set<Authority> authorities = new HashSet<>();

		if (password == null) {

			password = mobile;
		}

		String encryptedPassword = passwordEncoder.encode(password);

		if (login == null) {

			login = mobile;

			newUser.setLogin(login);

		} else {

			newUser.setLogin(login);
		}

		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		newUser.setEmail(email);
		newUser.setMobile(mobile);
		newUser.setLangKey(langKey);
		// new user is not active
		newUser.setActivated(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		newUser.setAuthorities(authorities);
		userRepository.save(newUser);
		userSearchRepository.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

	public User createUser(ManagedUserDTO managedUserDTO) {
		User user = new User();
		user.setLogin(managedUserDTO.getLogin());
		user.setFirstName(managedUserDTO.getFirstName());
		user.setLastName(managedUserDTO.getLastName());
		user.setEmail(managedUserDTO.getEmail());
		if (managedUserDTO.getLangKey() == null) {
			user.setLangKey("en"); // default language
		} else {
			user.setLangKey(managedUserDTO.getLangKey());
		}
		if (managedUserDTO.getAuthorities() != null) {
			Set<Authority> authorities = new HashSet<>();
			managedUserDTO.getAuthorities().stream()
					.forEach(authority -> authorities.add(authorityRepository.findOne(authority)));
			user.setAuthorities(authorities);
		}
		String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtil.generateResetKey());
		user.setResetDate(ZonedDateTime.now());
		user.setActivated(true);
		userRepository.save(user);
		userSearchRepository.save(user);
		log.debug("Created Information for User: {}", user);
		return user;
	}

	public void updateUserInformation(String firstName, String lastName, String email, String langKey) {
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setEmail(email);
			u.setLangKey(langKey);
			userRepository.save(u);
			userSearchRepository.save(u);
			log.debug("Changed Information for User: {}", u);
		});
	}

	public void deleteUserInformation(String login) {
		userRepository.findOneByLogin(login).ifPresent(u -> {
			userRepository.delete(u);
			userSearchRepository.delete(u);
			log.debug("Deleted User: {}", u);
		});
	}

	public void changePassword(String password) {
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			String encryptedPassword = passwordEncoder.encode(password);
			u.setPassword(encryptedPassword);
			userRepository.save(u);
			log.debug("Changed password for User: {}", u);
		});
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthoritiesByLogin(String login) {
		return userRepository.findOneByLogin(login).map(u -> {
			u.getAuthorities().size();
			return u;
		});
	}

	@Transactional(readOnly = true)
	public User getUserWithAuthorities(Long id) {
		User user = userRepository.findOne(id);
		user.getAuthorities().size(); // eagerly load the association
		return user;
	}

	@Transactional(readOnly = true)
	public User getUserWithAuthorities() {
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		user.getAuthorities().size(); // eagerly load the association
		return user;
	}

	/**
	 * Not activated users should be automatically deleted after 3 days.
	 * <p>
	 * This is scheduled to get fired everyday, at 01:00 (am).
	 * </p>
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void removeNotActivatedUsers() {
		ZonedDateTime now = ZonedDateTime.now();
		List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
		for (User user : users) {
			log.debug("Deleting not activated user {}", user.getLogin());
			userRepository.delete(user);
			userSearchRepository.delete(user);
		}
	}
}