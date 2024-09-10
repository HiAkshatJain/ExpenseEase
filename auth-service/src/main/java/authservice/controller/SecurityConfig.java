package authservice.controller;

import authservice.auth.JwtAuthFilter;
import authservice.eventProducer.UserInfoProducer;
import authservice.repository.UserRepository;
import authservice.service.UserDetailsServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 * Configures the security filter chain, authentication provider, and other security settings.
 */
@Slf4j
@Configuration
@EnableMethodSecurity // Enables method-level security, such as @PreAuthorize annotations
@Data
public class SecurityConfig  {

    @Autowired
    private final PasswordEncoder passwordEncoder; // Bean for encoding passwords

    @Autowired
    private final UserDetailsServiceImpl userDetailsServiceImpl; // Custom user details service implementation

    @Autowired
    private final UserInfoProducer userInfoProducer; // Producer for user information events

    /**
     * Bean for UserDetailsService.
     * Creates an instance of UserDetailsServiceImpl with the provided repository, password encoder, and producer.
     *
     * @param userRepository Repository for user data access
     * @param passwordEncoder Encoder for encoding passwords
     * @return a UserDetailsService instance
     */
    @Bean
    @Autowired
    public UserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserDetailsServiceImpl(userRepository, passwordEncoder, userInfoProducer);
    }

    /**
     * Bean for configuring the security filter chain.
     * Sets up HTTP security settings, including CORS, CSRF, session management, and filter chains.
     *
     * @param http the HttpSecurity object for configuring HTTP security
     * @param jwtAuthFilter the custom JWT authentication filter
     * @return a SecurityFilterChain object
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection
//                .cors(CorsConfigurer::disable) // Disables CORS configuration
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refreshToken", "/api/v1/auth/signup", "/api/v1/auth/health").permitAll() // Allow unauthenticated access to these endpoints
//                        .anyRequest().authenticated() // All other requests require authentication
//                )
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sets session management to stateless (no session creation)
//                .httpBasic(Customizer.withDefaults()) // Configures HTTP Basic authentication
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Adds JWT filter before the default UsernamePasswordAuthenticationFilter
//                .authenticationProvider(authenticationProvider()) // Sets the custom authentication provider
//                .build();

        return http
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection
                .cors(cors -> cors.disable()) // Disables CORS configuration
                .authorizeRequests(auth -> auth
                        .anyRequest().permitAll() // Permit all requests without authentication
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                .build();

    }

    /**
     * Bean for configuring the authentication provider.
     * Sets up a DaoAuthenticationProvider with the custom user details service and password encoder.
     *
     * @return an AuthenticationProvider instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceImpl); // Sets the custom user details service
        authenticationProvider.setPasswordEncoder(passwordEncoder); // Sets the password encoder
        return authenticationProvider;
    }

    /**
     * Bean for configuring the AuthenticationManager.
     * Retrieves the AuthenticationManager from the AuthenticationConfiguration.
     *
     * @param config the AuthenticationConfiguration object
     * @return an AuthenticationManager instance
     * @throws Exception if an error occurs during retrieval
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}