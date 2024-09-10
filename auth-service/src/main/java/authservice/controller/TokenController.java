package authservice.controller;

import authservice.entities.RefreshToken;
import authservice.request.AuthRequestDTO;
import authservice.request.RefreshTokenRequestDTO;
import authservice.response.JwtResponseDTO;
import authservice.service.JwtService;
import authservice.service.RefreshTokenService;
import authservice.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

/**
 * Controller for handling authentication and token management.
 */
@Controller
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager; // Handles authentication using username and password

    @Autowired
    private RefreshTokenService refreshTokenService; // Service for managing refresh tokens

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Service for loading user details

    @Autowired
    private JwtService jwtService; // Service for generating and validating JWT tokens

    /**
     * Endpoint to authenticate a user and generate tokens.
     *
     * @param authRequestDTO Contains username and password for authentication
     * @return ResponseEntity containing the JWT and refresh token if authentication is successful, otherwise an error message
     */
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        // Authenticate the user with provided username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Create a new refresh token for the authenticated user
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());

            // Retrieve user ID and verify if the refresh token was created successfully
            String userId = userDetailsService.getUserByUsername(authRequestDTO.getUsername());

            if (Objects.nonNull(userId) && Objects.nonNull(refreshToken)) {
                // Generate JWT token and return it along with the refresh token
                return new ResponseEntity<>(
                        JwtResponseDTO.builder()
                                .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                                .token(refreshToken.getToken())
                                .build(),
                        HttpStatus.OK
                );
            }
        }
        // Return an error response if authentication fails or if tokens cannot be created
        return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Endpoint to refresh the JWT token using a refresh token.
     *
     * @param refreshTokenRequestDTO Contains the refresh token to be used for generating a new JWT
     * @return JwtResponseDTO containing the new JWT and the provided refresh token
     * @throws RuntimeException if the refresh token is not found in the database
     */
    @PostMapping("/api/v1/auth/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        // Find the refresh token from the database and verify its expiration
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration) // Verify if the refresh token is still valid
                .map(RefreshToken::getUserInfo) // Retrieve user information associated with the refresh token
                .map(userInfo -> {
                    // Generate a new JWT for the user
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    System.out.println();
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!")); // Throw an exception if the token is not valid
    }
}
