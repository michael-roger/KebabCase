package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing building feature entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the
 * {@link BuildingFeatureEntity}.
 * </p>
 */

@Repository("BuildingFeatureRepository")
public interface BuildingFeatureRepositoryInterface extends
        JpaRepository<BuildingFeatureEntity, Integer> {
}
