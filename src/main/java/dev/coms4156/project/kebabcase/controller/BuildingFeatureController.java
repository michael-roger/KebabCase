package dev.coms4156.project.kebabcase.controller;

import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing building feature entities.
 * <p>
 * Provides endpoints to get all building features.
 * </p>
 * 
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li><strong>GET /building-features</strong>: Finds all building features.</li>
 * </ul>
 *
 */

@RestController
public class BuildingFeatureController {
  
  private final BuildingFeatureRepositoryInterface buildingFeatureRepository;

  /**
   * Constructs a new BuildingController.
   *
   * @param buildingFeatureRepository the repository used for building features
   */

  public BuildingFeatureController(
      BuildingFeatureRepositoryInterface buildingFeatureRepository
  ) {
    this.buildingFeatureRepository = buildingFeatureRepository;
  }

  /**
   * Retrieves a list of all building features from the repository,
   * returns all building features in the repository as a list.
   *
   * @return ResponseEntity containing the list of building features as a JSON response.
   *         Returns a 200 OK status if building features are found, or 204 No Content if no
   *         building features exist in the repository.
   */

  @GetMapping("/building-features")
  public ResponseEntity<List<BuildingFeatureEntity>> getBuildingFeatures() {

    List<BuildingFeatureEntity> featureEntities =
            this.buildingFeatureRepository.findAll();

    if (featureEntities.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(featureEntities);
  }

}
