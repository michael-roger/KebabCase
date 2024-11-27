package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing building entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations on 
 * {@link BuildingEntity}. It also includes a custom query method for finding a 
 * building by its address, city, state, and zip code.
 * </p>
 *
 *
 * @see BuildingEntity
 */

@Repository("BuildingRepository")
public interface BuildingRepositoryInterface extends JpaRepository<BuildingEntity, Integer> {

  /**
   * Finds a building by its address, city, state, and zip code.
   *
   * @param address the street address of the building
   * @param city    the city where the building is located
   * @param state   the state where the building is located
   * @param zipCode the zip code of the building
   * @return an {@link Optional} containing the building if found, or empty if not found
   */

  Optional<BuildingEntity> findByAddressAndCityAndStateAndZipCode(String address,
                                                                  String city,
                                                                  String state,
                                                                  String zipCode);

  /**
   * Finds the building with the given address.
   *
   * @param address the  address of the building
   * @return an {@link Optional} containing the building if found, or empty if not found
   */

  Optional<BuildingEntity> findByAddress(String address);

  /**
   * Finds a list of buildings with the given city.
   *
   * @param city the city of the selected buildings
   * @return a list of buildings containing the city if found, or empty list if not found
   */

  List<BuildingEntity> findByCity(String city);

  /**
   * Finds a list of buildings with the given state.
   *
   * @param state the state of the selected buildings
   * @return a list of buildings containing the state if found, or empty list if not found
   */
  List<BuildingEntity> findByState(String state);
}
