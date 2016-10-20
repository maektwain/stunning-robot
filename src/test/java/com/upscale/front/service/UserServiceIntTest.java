package com.upscale.front.service;

import com.upscale.front.FrontendApp;
import com.upscale.front.data.OauthData;
import com.upscale.front.domain.OauthClientDetails;
import com.upscale.front.domain.User;
import com.upscale.front.repository.UserRepository;
import com.upscale.front.service.util.RandomUtil;
import com.upscale.front.web.rest.dto.OauthClientDetailsDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FrontendApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class UserServiceIntTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private DocumentService documentService;

    @Inject
    private SMSService smsService;

    @Test
    public void assertThatUserMustExistToResetPassword() {
        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();

        maybeUser = userService.requestPasswordReset("admin@localhost");
        assertThat(maybeUser.isPresent()).isTrue();

        assertThat(maybeUser.get().getEmail()).isEqualTo("admin@localhost");
        assertThat(maybeUser.get().getResetDate()).isNotNull();
        assertThat(maybeUser.get().getResetKey()).isNotNull();
    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();
        userRepository.delete(user);
    }

    @Test
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");

        ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(25);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);

        userRepository.save(user);

        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());

        assertThat(maybeUser.isPresent()).isFalse();

        userRepository.delete(user);
    }

    @Test
    public void assertThatResetKeyMustBeValid() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");

        ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(25);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");
        userRepository.save(user);
        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser.isPresent()).isFalse();
        userRepository.delete(user);
    }

    @Test
    public void assertThatUserCanResetPassword() {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        String oldPassword = user.getPassword();
        ZonedDateTime daysAgo = ZonedDateTime.now().minusHours(2);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userRepository.save(user);
        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser.isPresent()).isTrue();
        assertThat(maybeUser.get().getResetDate()).isNull();
        assertThat(maybeUser.get().getResetKey()).isNull();
        assertThat(maybeUser.get().getPassword()).isNotEqualTo(oldPassword);

        userRepository.delete(user);
    }

    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {
        userService.removeNotActivatedUsers();
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        assertThat(users).isEmpty();
    }


    @Test
    public void testApplicationCreationByUser() throws NoSuchAlgorithmException {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        OauthClientDetailsDTO oauthClientDetailsDTO = new OauthClientDetailsDTO("Test Application", "This application is for testing purpose only", "https://theupscale.co.in/test");
        assertThat(userService.createApplication(oauthClientDetailsDTO, user)).isNotNull();
    }

    @Test
    public void testToRetrieveApplicationOauthToken() throws NoSuchAlgorithmException {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        OauthClientDetailsDTO oauthClientDetailsDTO = new OauthClientDetailsDTO("Test Application", "This application is for testing purpose only", "https://theupscale.co.in/test");
        userService.createApplication(oauthClientDetailsDTO, user);
        List<OauthData> oauthData = userService.retrieveApplications(user);
        assertThat(oauthData).isNotNull();
    }

    @Test
    public void testToDeleteApplication() throws NoSuchAlgorithmException {
        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost","9899318697", "en-US");
        OauthClientDetailsDTO oauthClientDetailsDTO = new OauthClientDetailsDTO("Test Application", "This application is for testing purpose only", "https://theupscale.co.in/test");
        userService.createApplication(oauthClientDetailsDTO, user);
        OauthClientDetails oauthClientDetails = userService.retrieveApplicationsByName("Test Application", user);
        assertThat(userService.deleteApplication(oauthClientDetails)).isNotNull();
    }



}
