package authservice.service;

import authservice.entities.RefreshToken;
import authservice.entities.UserInfo;
import authservice.repository.RefreshTokenRepository;
import authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Creates a new refresh token for the given username.
     *
     * @param username the username for which to create the refresh token
     * @return the created RefreshToken
     */
    public RefreshToken createRefreshToken(String username) {
        UserInfo userInfoExtracted = userRepository.findByUsername(username);
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfoExtracted)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // Set expiry to 10 minutes from now
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Finds a refresh token by its token value.
     *
     * @param token the token value to search for
     * @return an Optional containing the RefreshToken if found
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies if the refresh token is expired.
     * If expired, deletes the token and throws an exception.
     *
     * @param token the RefreshToken to check
     * @return the RefreshToken if it is not expired
     * @throws RuntimeException if the token is expired
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }
}
