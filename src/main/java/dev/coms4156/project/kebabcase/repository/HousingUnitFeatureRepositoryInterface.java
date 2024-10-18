package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing housing unit feature entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the
 * {@link HousingUnitFeatureEntity}.
 * </p>
 */

@Repository("HousingUnitFeatureRepository")
public interface HousingUnitFeatureRepositoryInterface extends
        JpaRepository<HousingUnitFeatureEntity, Integer> {
}
