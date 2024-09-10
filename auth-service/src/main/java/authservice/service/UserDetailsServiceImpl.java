package authservice.service;

import authservice.entities.UserInfo;
import authservice.eventProducer.UserInfoEvent;
import authservice.eventProducer.UserInfoProducer;
import authservice.model.UserInfoDto;
import authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Collate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the UserDetailsService interface.
 * Provides methods for user authentication, user registration, and user management.
 */
@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository; // Repository for accessing user data

    @Autowired
    private final PasswordEncoder passwordEncoder; // Encoder for hashing user passwords

    @Autowired
    private final UserInfoProducer userInfoProducer; // Producer for sending user-related events

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class); // Logger for debugging and information

    /**
     * Loads user details by username.
     *
     * @param username the username of the user to be loaded
     * @return UserDetails object representing the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username); // Fetch user from repository
        if (user == null) {
            log.error("Username not found: " + username); // Log an error if user is not found
            throw new UsernameNotFoundException("could not found user..!!"); // Throw exception if user is not found
        }
        log.info("User Authenticated Successfully..!!!"); // Log information if user is found
        return new CustomUserDetails(user); // Return a custom UserDetails object
    }

    /**
     * Checks if a user already exists based on the provided UserInfoDto.
     *
     * @param userInfoDto the user information to check
     * @return UserInfo object if user exists, otherwise null
     */
    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto) {
        return userRepository.findByUsername(userInfoDto.getUsername()); // Check user existence in repository
    }

    /**
     * Signs up a new user.
     *
     * @param userInfoDto the DTO containing user registration details
     * @return the ID of the newly created user, or null if the user already exists
     */
    public String signupUser(UserInfoDto userInfoDto) {
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword())); // Encode the password
        if (Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))) {
            return null; // Return null if the user already exists
        }
        String userId = UUID.randomUUID().toString(); // Generate a new unique user ID
        UserInfo userInfo = new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>());
        userRepository.save(userInfo); // Save new user to the repository
//        userInfoProducer.sendEventToKafka(userInfoEventToPublish(userInfoDto, userId)); // Optional: Send user info event to Kafka
        return userId; // Return the new user's ID
    }

    /**
     * Retrieves the user ID by username.
     *
     * @param userName the username of the user
     * @return the user ID if the user is found, otherwise null
     */
    public String getUserByUsername(String userName){
        return Optional.of(userRepository.findByUsername(userName))
                .map(UserInfo::getUserId) // Map to user ID if user is found
                .orElse(null); // Return null if user is not found
    }

    /**
     * Creates a UserInfoEvent for publishing user information.
     *
     * @param userInfoDto the DTO containing user information
     * @param userId the ID of the user
     * @return a UserInfoEvent object with the user's details
     */
    private UserInfoEvent userInfoEventToPublish(UserInfoDto userInfoDto, String userId){
        return UserInfoEvent.builder()
                .userId(userId)
                .firstName(userInfoDto.getUsername()) // Assuming username as first name
                .lastName(userInfoDto.getLastName())
                .email(userInfoDto.getEmail())
                .phoneNumber(userInfoDto.getPhoneNumber())
                .build(); // Build and return the UserInfoEvent
    }
}
