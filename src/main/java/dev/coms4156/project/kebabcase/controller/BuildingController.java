package dev.coms4156.project.kebabcase.controller;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST controller for managing building entities and their associated features.
 * <p>
 * Provides endpoints to create buildings, update details, and manage feature associations.
 * Validates that buildings can't be created with duplicate addresses and supports partial
 * content updates for invalid feature IDs.
 * </p>
 * 
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li><strong>POST /building</strong>: Creates a building and associates features.</li>
 *   <li><strong>PATCH /building/{id}</strong>: Updates a building, optionally adds/removes
 *       removes features.</li>
 * </ul>
 * 
 * <h2>Features:</h2>
 * <p>
 * Features are entities that can be added or removed when updating a building. Invalid feature IDs
 * return HTTP 206 Partial Content.
 * </p>
 * 
 * <h2>Error Handling:</h2>
 * <p>
 * - HTTP 404: Building or feature not found.<br>
 * - HTTP 400: No fields provided for update.<br>
 * - HTTP 409: Duplicate building address.<br>
 * - HTTP 206: Some feature IDs are invalid.
 * </p>
 */
@RestController
public class BuildingController {
  
  private final BuildingRepositoryInterface buildingRepository;

  private final BuildingFeatureRepositoryInterface buildingFeatureRepository;

  private final BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository;

  private final ObjectMapper objectMapper;

  /**
   * Constructs a new BuildingController.
   *
   * @param buildingRepository the repository used to interact with building entities
   * @param buildingFeatureRepository the repository used for building features
   * @param buildingFeatureMappingRepository the repository for mapping building features
   */
  public BuildingController(
      BuildingRepositoryInterface buildingRepository,
      BuildingFeatureRepositoryInterface buildingFeatureRepository,
      BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository,
      ObjectMapper objectMapper) {
    this.buildingRepository = buildingRepository;
    this.buildingFeatureRepository = buildingFeatureRepository;
    this.buildingFeatureMappingRepository = buildingFeatureMappingRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * Retrieves a specific building by its ID and returns the details as a JSON object.
   *
   * @param id The ID of the building to retrieve.
   * @return An ObjectNode JSON object containing the building details.
   * @throws ResponseStatusException if the building with the given ID is not found,
   *     with an HTTP status of 404.
   */
  @GetMapping("/building/{id}")
  public ObjectNode getBuildingById(@PathVariable int id) {

    Optional<BuildingEntity> buildingRepositoryResult = this.buildingRepository.findById(id);
    if (buildingRepositoryResult.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Building with id " + id + " not found"
      );
    }

    BuildingEntity building = buildingRepositoryResult.get();

    ObjectNode json = this.objectMapper.createObjectNode();
    json.put("id", building.getId());
    json.put("address", building.getAddress());
    json.put("city", building.getCity());
    json.put("state", building.getState());
    json.put("zip_code", building.getZipCode());
    json.put("created_datetime", building.getCreatedDatetime().toString());
    json.put("modified_datetime", building.getModifiedDatetime().toString());

    return json;
  }
  
  /**
   * Updates the information of an existing building by its ID.
   * 
   * <p>Updates only the provided fields. If no fields are provided, an HTTP 400 Bad Request will 
   * be returned. New building features can also be added, and existing features can be removed.
   * </p>
   *
   * @param id the ID of the building to update
   * @param address the new address of the building (optional)
   * @param city the new city of the building (optional)
   * @param state the new state of the building (optional)
   * @param zipCode the new zip code of the building (optional)
   * @param addFeatures a list of feature IDs to associate with the building (optional)
   * @param removeFeatures a list of feature IDs to disassociate from the building (optional)
   * @return a {@link ResponseEntity} indicating the result of the update. If all updates succeed, 
   *     an HTTP 200 OK is returned. If some feature IDs are invalid, an HTTP 206 Partial Content
   *     is returned with a message listing the invalid feature IDs.
   * @throws ResponseStatusException if the building with the given ID is not found or if no 
   *     fields are provided for update
   */
  @PatchMapping("/building/{id}")
  public ResponseEntity<?> updateBuilding(
      @PathVariable int id, 
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String state,
      @RequestParam(required = false) String zipCode,
      @RequestParam(required = false) List<Integer> addFeatures,
      @RequestParam(required = false) List<Integer> removeFeatures
  ) {

    /* Check if building exists */
    Optional<BuildingEntity> buildingResult = buildingRepository.findById(id);

    if (buildingResult.isEmpty()) {
      String errorMessage = "Building not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    BuildingEntity building = buildingResult.get();

    /* Check if the user entered anything to update */
    if (address == null 
        && city == null
        && state == null
        && zipCode == null
        && addFeatures == null
        && removeFeatures == null) {
      String errorMessage = "No fields provided for update";
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    /* Check if there is an ID in both add/remove */
    if (addFeatures != null && removeFeatures != null) {
      Set<Integer> addFeaturesSet = new HashSet<>(addFeatures);
      Set<Integer> removeFeaturesSet = new HashSet<>(removeFeatures);

      addFeaturesSet.retainAll(removeFeaturesSet);

      if (!addFeaturesSet.isEmpty()) {
        String errorMessage = "Conflict: Feature IDs present in both add " 
                                 + "and remove lists: " + addFeaturesSet;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
      }
    }

    /* Start updating */
    if (address != null) {
      building.setAddress(address);
    }
    if (city != null) {
      building.setCity(city);
    }
    if (state != null) {
      building.setState(state);
    }
    if (zipCode != null) {
      building.setZipCode(zipCode);
    }

    building.setModifiedDatetime(OffsetDateTime.now());

    buildingRepository.save(building);

    Set<Integer> invalidFeatures = new HashSet<>();

    /* Insert new building feature to building-feature mapping if exists */
    if (addFeatures != null) {
      for (Integer featureId : addFeatures) {
        BuildingFeatureBuildingMappingEntity buildingMapFeature = 
            new BuildingFeatureBuildingMappingEntity();

        Optional<BuildingFeatureEntity> featureResult = 
            this.buildingFeatureRepository.findById(featureId);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureId);
        } else {
          BuildingFeatureEntity feature = featureResult.get();

          Optional<BuildingFeatureBuildingMappingEntity> existingMapping =
              this.buildingFeatureMappingRepository
                  .findByBuildingAndBuildingFeature(building, feature);

          if (existingMapping.isEmpty()) {
            buildingMapFeature.setBuilding(building);
            buildingMapFeature.setBuildingFeature(feature);

            buildingFeatureMappingRepository.save(buildingMapFeature);
          }
        }
      }
    }

    /* Remove features */
    if (removeFeatures != null) {
      for (Integer featureId : removeFeatures) {
        Optional<BuildingFeatureEntity> featureResult = 
            this.buildingFeatureRepository.findById(featureId);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureId);
        } else {
          BuildingFeatureEntity feature = featureResult.get();

          Optional<BuildingFeatureBuildingMappingEntity> existingMapping =
              this.buildingFeatureMappingRepository
                  .findByBuildingAndBuildingFeature(building, feature);

          if (existingMapping.isPresent()) {
            buildingFeatureMappingRepository.delete(existingMapping.get());
          }
        }
      }
    }

    if (address == null
        && city == null
        && state == null
        && zipCode == null
        && addFeatures != null
        && removeFeatures != null
        && invalidFeatures.size() == (addFeatures.size() + removeFeatures.size())) {
      String errorMessage = "Could not find any of the building features requested.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);          
    }

    if (!invalidFeatures.isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body("Building updated, but the following feature IDs were not found: " 
                  + invalidFeatures);
    }

    return ResponseEntity.ok("Building info has been successfully updated!");
  }

  /**
   * Creates a new building entity and saves it to the repository.
   * 
   * <p>If a building with the same address, city, state, and zip code already exists, an HTTP 409 
   * Conflict is returned. Optionally, feature IDs can be associated with the building at creation. 
   * If any feature IDs are invalid, an HTTP 206 Partial Content is returned.
   * </p>
   *
   * @param address the address of the new building
   * @param city the city of the new building
   * @param state the state of the new building
   * @param zipCode the zip code of the new building
   * @param features a list of feature IDs to associate with the new building (optional)
   * @return a {@link ResponseEntity} containing the result of the creation and the new 
   *     building's ID. If any feature IDs are invalid, an HTTP 206 Partial Content is returned.
   * @throws ResponseStatusException if a building with the same address, city, state, 
   *     and zip code already exists
   */
  @PostMapping("/building")
  public ResponseEntity<?> createBuilding(
      @RequestParam String address,
      @RequestParam String city,
      @RequestParam String state,
      @RequestParam String zipCode,
      @RequestParam(required = false) List<Integer> features
  ) {

    /* Check if the building already exists */
    Optional<BuildingEntity> existingBuilding = 
        buildingRepository.findByAddressAndCityAndStateAndZipCode(address, city, state, zipCode);

    if (existingBuilding.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("A building with the same address already exists.");
    }

    /* Create building */
    BuildingEntity newBuilding = new BuildingEntity();

    newBuilding.setAddress(address);
    newBuilding.setCity(city);
    newBuilding.setState(state);
    newBuilding.setZipCode(zipCode);
    newBuilding.setCreatedDatetime(OffsetDateTime.now());
    newBuilding.setModifiedDatetime(OffsetDateTime.now());

    BuildingEntity savedBuilding = buildingRepository.save(newBuilding);

    /* Add building features */
    List<Integer> invalidFeatures = new ArrayList<>();

    if (features != null) {
      for (Integer featureId : features) {
        BuildingFeatureBuildingMappingEntity buildingMapFeature = 
            new BuildingFeatureBuildingMappingEntity();

        Optional<BuildingFeatureEntity> featureResult = 
            this.buildingFeatureRepository.findById(featureId);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureId);
        } else {
          BuildingFeatureEntity feature = featureResult.get();

          buildingMapFeature.setBuilding(savedBuilding);
          buildingMapFeature.setBuildingFeature(feature);

          buildingFeatureMappingRepository.save(buildingMapFeature);
        }
      }
    }

    if (!invalidFeatures.isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body("Building created, but the following feature IDs were not found: " 
                  + invalidFeatures);
    }

    String response = "Building was added succesfully! Building ID: " 
                          + savedBuilding.getId().toString();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}