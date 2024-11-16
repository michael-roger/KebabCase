package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.HousingUnitUserMappingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing mappings between users and housing units.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on 
 * {@link HousingUnitUserMappingEntity}. It includes a custom query method to find all
 * housing unit mappings associated with a specific user by their ID.
 * </p>
 *
 * @see HousingUnitUserMappingEntity
 */
@Repository("HousingUnitUserMappingRepository")
public interface HousingUnitUserMappingRepositoryInterface 
    extends JpaRepository<HousingUnitUserMappingEntity, Integer> {

  /**
   * Finds all mappings between a user and housing units by the user's ID.
   *
   * @param userId the ID of the user for whom to find associated housing units
   * @return a list of {@link HousingUnitUserMappingEntity} containing all housing unit mappings
   *         associated with the specified user, or an empty list if none are found
   */
  List<HousingUnitUserMappingEntity> findByUserId(int userId);
}
