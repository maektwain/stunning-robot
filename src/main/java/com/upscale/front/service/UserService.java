package com.upscale.front.service;

import com.upscale.front.data.OauthData;
import com.upscale.front.domain.Authority;
import com.upscale.front.domain.OauthClientDetails;
import com.upscale.front.domain.User;
import com.upscale.front.repository.AuthorityRepository;
import com.upscale.front.repository.OauthClientRepository;
import com.upscale.front.repository.UserRepository;
import com.upscale.front.repository.search.OauthClientDetailsSearchRepository;
import com.upscale.front.repository.search.UserSearchRepository;
import com.upscale.front.security.AuthoritiesConstants;
import com.upscale.front.security.SecurityUtils;
import com.upscale.front.service.util.RandomUtil;
import com.upscale.front.web.rest.dto.ManagedUserDTO;
import com.upscale.front.web.rest.dto.OauthClientDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

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
    private OauthClientDetailsSearchRepository oauthClientDetailsSearchRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private SMSService smsService;

    @Inject
    private OauthClientRepository oauthRepository;



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



			userRepository.save(user);
			userSearchRepository.save(user);

			log.debug("Activating User Through OTP", user);

			// Creating a self service user

			//createSelfService(user.getLogin(), user.getPassword());

			return user;
		});
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

    public OauthClientDetails createApplication(OauthClientDetailsDTO oauthClientDetailsDTO,User u){
        OauthClientDetails oauthClientDetails = new OauthClientDetails();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date today = Calendar.getInstance().getTime();
        String date = sdf.format(today);
        oauthClientDetails.setApplicationname(date + oauthClientDetailsDTO.getApplicationname());
        byte[] encode = Base64.encode(oauthClientDetailsDTO.getApplicationname().getBytes());
        oauthClientDetails.setUser(u);
        oauthClientDetails.setClientsecret(encode.toString());
        oauthClientDetails.setScope("read", "write");
        oauthClientDetails.setAuthorizedgranttypes("password","refresh_token" ,"authorization_code" ,"implicit");
        oauthClientDetails.setAuthorities("ROLE_USER");
        oauthClientDetails.setAccesstokenvalidity(1800);
        oauthClientDetails.setWebserverredirecturi(oauthClientDetailsDTO.getCallbackurl());
        oauthRepository.save(oauthClientDetails);
        /**
         * TBD The problem which is
         * That inside the domain/model we need to assign an ID, like Primary Key but given the fact that primary key can also be the application name
         * which can be unique
         */

        //oauthClientDetailsSearchRepository.save(oauthClientDetails);
        return oauthClientDetails;
    }

    public OauthData retrieveApplications(User u) {
        OauthClientDetails oauthClientDetails = oauthRepository.findAllByUser(u).get();
        OauthData oauthData = new OauthData();

        //oauthData.setId(oauthClientDetails.getId());
        oauthData.setCliendId(oauthClientDetails.getApplicationname());
        oauthData.setClientToken(oauthClientDetails.getClientsecret());
        return oauthData;
    }


    public OauthClientDetails retrieveApplicationsByName(String applicationname, User u) {
        OauthClientDetails oauthClientDetails = oauthRepository.findOneByApplicationNameAndUser(applicationname, u).get();
        return oauthClientDetails;
    }

    public String deleteApplication(OauthClientDetails oauthClientDetails) {
        oauthRepository.delete(oauthClientDetails);
        return "Deleted";
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
