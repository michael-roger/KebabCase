package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing user entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the
 * {@link UserEntity}.
 * </p>
 */

@Repository("UserRepository")
public interface UserRepositoryInterface extends JpaRepository<UserEntity, Integer> {
}
