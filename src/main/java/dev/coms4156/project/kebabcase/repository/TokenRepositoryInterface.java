package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.TokenEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing token entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the
 * {@link TokenEntity}.
 * </p>
 */

@Repository("TokenRepository")
public interface TokenRepositoryInterface extends JpaRepository<TokenEntity, Integer> {
}
