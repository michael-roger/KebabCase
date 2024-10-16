package dev.coms4156.project.kebabcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;

/**
 * Repository interface for managing mappings between housing units and their associated features.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on the 
 * {@link HousingUnitFeatureHousingUnitMappingEntity}. It also includes a custom query method 
 * for finding mappings by housing unit and feature.
 * </p>
 * 
 * @see HousingUnitFeatureHousingUnitMappingEntity
 * @see HousingUnitEntity
 * @see HousingUnitFeatureEntity
 */
@Repository("HousingUnitFeatureHousingUnitMappingRepository")
public interface HousingUnitFeatureHousingUnitMappingRepositoryInterface 
      extends JpaRepository<HousingUnitFeatureHousingUnitMappingEntity, Integer> {

  /**
   * Finds a mapping between a housing unit and a feature.
   * 
   * @param housingUnit the housing unit entity
   * @param housingUnitFeature the feature entity associated with the housing unit
   * @return an {@link Optional} containing the found mapping or empty if not found
   */
  Optional<HousingUnitFeatureHousingUnitMappingEntity> 
      findByHousingUnitAndHousingUnitFeature(HousingUnitEntity housingUnit, 
                                                HousingUnitFeatureEntity housingUnitFeature);

}
