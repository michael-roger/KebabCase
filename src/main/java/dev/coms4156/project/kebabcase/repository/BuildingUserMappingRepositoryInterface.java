package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingUserMappingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing mappings between users and buildings.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on 
 * {@link BuildingUserMappingEntity}. It includes a custom query method to find all
 * building mappings associated with a specific user by their ID.
 * </p>
 *
 * @see BuildingUserMappingEntity
 */
@Repository("BuildingUserMappingRepository")
public interface BuildingUserMappingRepositoryInterface 
    extends JpaRepository<BuildingUserMappingEntity, Integer> {
        
  /**
   * Finds all mappings between a user and buildings by the user's ID.
   *
   * @param userId the ID of the user for whom to find associated buildings
   * @return a list of {@link BuildingUserMappingEntity} containing all building mappings
   *         associated with the specified user, or an empty list if none are found
   */
  List<BuildingUserMappingEntity> findByUserId(int userId);
}