package authservice.controller;

import authservice.entities.RefreshToken;
import authservice.model.UserInfoDto;
import authservice.response.JwtResponseDTO;
import authservice.service.JwtService;
import authservice.service.RefreshTokenService;
import authservice.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Controller for handling authentication-related endpoints.
 * Provides endpoints for user signup and checking user authentication status.
 */
@AllArgsConstructor
@RestController
public class AuthController {

    @Autowired
    private JwtService jwtService;  // Service for JWT operations

    @Autowired
    private RefreshTokenService refreshTokenService;  // Service for managing refresh tokens

    @Autowired
    private UserDetailsServiceImpl userDetailsService;  // Service for user management

    /**
     * Endpoint for user signup.
     *
     * @param userInfoDto Data transfer object containing user information
     * @return ResponseEntity containing JWT token, refresh token, and user ID if successful, or an error message if not
     */
    @PostMapping("/auth/v1/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserInfoDto userInfoDto){
        try {
            // Attempt to sign up the user and obtain the user ID
            String userId = userDetailsService.signupUser(userInfoDto);

            // If userId is null, the user already exists
            if (Objects.isNull(userId)) {
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }

            // Create a new refresh token for the user
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());

            // Generate a JWT token for the user
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());

            // Return the JWT token, refresh token, and user ID in the response
            return new ResponseEntity<>(JwtResponseDTO.builder()
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken())
                    .userId(userId)
                    .build(), HttpStatus.OK);
        } catch (Exception ex) {
            // Handle any exceptions that occur during user signup
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to check if the user is authenticated and return their user ID.
     *
     * @return ResponseEntity containing the user ID if authenticated, or an unauthorized status if not
     */
    @GetMapping("/auth/v1/ping")
    public ResponseEntity<String> ping(){
        // Retrieve the current authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is not null and the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Obtain the user ID based on the username from the authentication object
            String userId = userDetailsService.getUserByUsername(authentication.getName());

            // If the user ID is not null, return it in the response
            if (Objects.nonNull(userId)) {
                return ResponseEntity.ok(userId);
            }
        }

        // If the user is not authenticated, return an unauthorized status
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}
