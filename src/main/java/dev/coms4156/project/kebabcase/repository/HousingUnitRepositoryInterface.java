package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing housing unit entities within buildings.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the 
 * {@link HousingUnitEntity}. It also includes custom query methods for finding housing 
 * units by building and by building and unit number.
 * </p>
 * 
 * @see HousingUnitEntity
 * @see BuildingEntity
 */
@Repository("HousingUnitRepository")
public interface HousingUnitRepositoryInterface extends JpaRepository<HousingUnitEntity, Integer> {

  /**
   * Retrieves a list of housing units associated with a specific building.
   * 
   * @param building the building entity to find housing units for
   * @return a list of {@link HousingUnitEntity} associated with the building
   */
  List<HousingUnitEntity> findByBuilding(BuildingEntity building);

  /**
   * Finds a housing unit by its building and unit number.
   * 
   * @param building the building entity
   * @param unitNumber the unit number of the housing unit
   * @return an {@link Optional} containing the housing unit if found, or empty if not found
   */
  Optional<HousingUnitEntity> 
      findByBuildingAndUnitNumber(BuildingEntity building, String unitNumber);
}
