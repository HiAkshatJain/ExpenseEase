package authservice.repository;

import authservice.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserInfo} entities.
 * This interface extends CrudRepository to provide basic CRUD operations.
 */
@Repository
public interface UserRepository extends CrudRepository<UserInfo, Long> {

    /**
     * Finds a {@link UserInfo} by its username.
     *
     * @param username the username to search for
     * @return the {@link UserInfo} associated with the provided username
     */
    public UserInfo findByUsername(String username);
}
