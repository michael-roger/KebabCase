package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for managing user entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the
 * {@link UserEntity}.
 * </p>
 */

@Repository("UserRepository")
public interface UserRepositoryInterface extends JpaRepository<UserEntity, Integer> {
    
    /**
     * Finds a user by according to email address.
     *
     * @param emailAddress the email address to search for
     * @return an Optional containing the UserEntity if found, otherwise empty
     */
    Optional<UserEntity> findByEmailAddress(String emailAddress);

}
