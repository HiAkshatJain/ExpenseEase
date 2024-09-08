package authservice.repository;

import authservice.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link RefreshToken} entities.
 * This interface extends CrudRepository to provide basic CRUD operations.
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

    /**
     * Finds a {@link RefreshToken} by its token string.
     *
     * @param token the token string to search for
     * @return an {@link Optional} containing the found {@link RefreshToken}, or empty if not found
     */
    Optional<RefreshToken> findByToken(String token);

}
