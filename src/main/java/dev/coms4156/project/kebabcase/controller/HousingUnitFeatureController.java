package dev.coms4156.project.kebabcase.controller;

import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing housing unit feature entities.
 * <p>
 * Provides endpoints to get all housing unit features.
 * </p>
 * 
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li><strong>GET /housing-unit-features</strong>: Finds all housing unit features.</li>
 * </ul>
 *
 */

@RestController
public class HousingUnitFeatureController {
  
  private final HousingUnitFeatureRepositoryInterface housingUnitFeatureRepository;

  /**
   * Constructs a new BuildingController.
   *
   * @param housingUnitFeatureRepository the repository used for building features
   */

  public HousingUnitFeatureController(
      HousingUnitFeatureRepositoryInterface housingUnitFeatureRepository
  ) {
    this.housingUnitFeatureRepository = housingUnitFeatureRepository;
  }

  /**
   * Retrieves a list of all housing units from the repository,
   * returns all housing units in the repository as a list.
   *
   * @return ResponseEntity containing the list of housing units as a JSON response.
   *         Returns a 200 OK status if housing units are found, or 204 No Content if no
   *         housing units exist in the repository.
   */

  @GetMapping("/housing-unit-features")
  public ResponseEntity<List<HousingUnitFeatureEntity>> getBuildingFeatures() {

    List<HousingUnitFeatureEntity> featureEntities =
            this.housingUnitFeatureRepository.findAll();

    if (featureEntities.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(featureEntities);
  }

}
