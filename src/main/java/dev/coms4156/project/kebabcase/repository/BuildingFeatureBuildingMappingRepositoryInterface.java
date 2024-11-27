package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing mappings between buildings and their features.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on 
 * {@link BuildingFeatureBuildingMappingEntity}. It also includes custom query
 * methods for finding a specific mapping by building and feature, and for finding
 * a specific mapping by feature id.
 * </p>
 *
 *
 * @see BuildingFeatureBuildingMappingEntity
 * @see BuildingEntity
 * @see BuildingFeatureEntity
 */

@Repository("BuildingFeatureBuildingMappingRepository")
public interface BuildingFeatureBuildingMappingRepositoryInterface 
    extends JpaRepository<BuildingFeatureBuildingMappingEntity, Integer> {

  /**
   * Finds the mapping between a building and a building feature.
   *
   *
   * @param building the building entity to find the feature mapping for
   * @param buildingFeature the building feature entity associated with the building
   * @return an {@link Optional} containing the mapping if found, or empty if not found
   */

  Optional<BuildingFeatureBuildingMappingEntity> 
      findByBuildingAndBuildingFeature(BuildingEntity building, 
                                          BuildingFeatureEntity buildingFeature);

  /**
   * Finds the entries in the building-building feature mapping that correspond to
   *     the desired building feature.
   *
   * @param buildingFeatureId the ID corresponding with the desired building feature
   * @return a list of {@link BuildingFeatureBuildingMappingEntity} containing
   *     all entries with the desired building feature, or empty if none found
   */
  List<BuildingFeatureBuildingMappingEntity> findByBuildingFeatureId(int buildingFeatureId);

  /**
   * Finds all mappings associated with a given building.
   *
   * @param building the building entity to find feature mappings for
   * @return a list of {@link BuildingFeatureBuildingMappingEntity} containing
   *     all mappings for the specified building, or an empty list if none found
   */
  List<BuildingFeatureBuildingMappingEntity> findByBuilding(BuildingEntity building);
}
