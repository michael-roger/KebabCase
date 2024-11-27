package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.ClientEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing client entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the
 * {@link ClientEntity}.
 * </p>
 */

@Repository("ClientRepository")
public interface ClientRepositoryInterface extends JpaRepository<ClientEntity, Integer> {

  /**
   * Finds a user by according to email address.
   *
   * @param name the client name to search for
   * @return an Optional containing the ClientEntity if found, otherwise empty
   */
  Optional<ClientEntity> findByName(String name);
}
