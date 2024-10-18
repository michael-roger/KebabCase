package dev.coms4156.project.kebabcase.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;

/** TODO */
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
   * @param objectMapper the object mapper used for JSON serialization
   */
  public BuildingController(
      BuildingRepositoryInterface buildingRepository,
      BuildingFeatureRepositoryInterface buildingFeatureRepository,
      BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository,
      ObjectMapper objectMapper
  ) {
    this.buildingRepository = buildingRepository;
    this.buildingFeatureRepository = buildingFeatureRepository;
    this.buildingFeatureMappingRepository = buildingFeatureMappingRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * Updates the information of an existing building by its ID.
   * Only the fields provided as request parameters will be updated.
   * If no fields are provided, an HTTP 400 Bad Request will be returned.
   * Additionally, new building features can be associated with the building.
   * If a list of feature IDs is provided, valid features will be added to the building.
   * If any feature ID in the list is not found, the update will still proceed, but an HTTP 206 Partial Content
   * will be returned, along with a message listing the invalid feature IDs.
   * 
   * @param id the ID of the building to update
   * @param address the new address of the building (optional)
   * @param city the new city of the building (optional)
   * @param state the new state of the building (optional)
   * @param zipCode the new zip code of the building (optional)
   * @param features a list of feature IDs to associate with the building (optional)
   * @return a {@link ResponseEntity} indicating the result of the update. If all updates succeed, 
   * an HTTP 200 OK is returned. If some feature IDs are invalid, an HTTP 206 Partial Content is returned 
   * with a message listing the invalid feature IDs.
   * @throws ResponseStatusException if the building with the given ID is not found or if no fields are provided for update
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
    if (address == null && 
        city == null && 
        state == null && 
        zipCode == null &&
        addFeatures == null &&
        removeFeatures == null) {
      String errorMessage = "No fields provided for update";
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    /* Check if there is an ID in both add/remove */
    if (addFeatures != null && removeFeatures != null) {
      Set<Integer> addFeaturesSet = new HashSet<>(addFeatures);
      Set<Integer> removeFeaturesSet = new HashSet<>(removeFeatures);

      addFeaturesSet.retainAll(removeFeaturesSet);

      if (!addFeaturesSet.isEmpty()) {
        String errorMessage = "Conflict: Feature IDs present in both add and remove lists: " + addFeaturesSet;
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
      for (Integer featureID : addFeatures) {
        BuildingFeatureBuildingMappingEntity buildingMapFeature = new BuildingFeatureBuildingMappingEntity();

        Optional<BuildingFeatureEntity> featureResult = this.buildingFeatureRepository.findById(featureID);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureID);
        } else {
          BuildingFeatureEntity feature = featureResult.get();

          Optional<BuildingFeatureBuildingMappingEntity> existingMapping =
            this.buildingFeatureMappingRepository.findByBuildingAndBuildingFeature(building, feature);

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
      for (Integer featureID : removeFeatures) {
        Optional<BuildingFeatureEntity> featureResult = this.buildingFeatureRepository.findById(featureID);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureID);
        } else {
          BuildingFeatureEntity feature = featureResult.get();

          Optional<BuildingFeatureBuildingMappingEntity> existingMapping =
              this.buildingFeatureMappingRepository.findByBuildingAndBuildingFeature(building, feature);

          if (existingMapping.isPresent()) {
            buildingFeatureMappingRepository.delete(existingMapping.get());
          }
        }
      }
    }

    if (address == null && 
        city == null && 
        state == null && 
        zipCode == null &&
        addFeatures != null &&
        removeFeatures != null &&
        invalidFeatures.size() == (addFeatures.size() + removeFeatures.size())) {
      String errorMessage = "Could not find any of the building features requested.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);          
    }

    if (!invalidFeatures.isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body("Building updated, but the following feature IDs were not found: " + invalidFeatures);
    }

    return ResponseEntity.ok("Building info has been successfully updated!");
  }

  /**
   * Creates a new building entity and saves it to the repository.
   * The new building's information must be provided as request parameters.
   * 
   * @param address the address of the new building
   * @param city the city of the new building
   * @param state the state of the new building
   * @param zipCode the zip code of the new building
   * @return a {@link ResponseEntity} containing the result of the creation and the new building's ID
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
    Optional<BuildingEntity> existingBuilding = buildingRepository.findByAddressAndCityAndStateAndZipCode(address, city, state, zipCode);

    if (existingBuilding.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("A building with the same address already exists.");
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
      for (Integer featureID : features) {
        BuildingFeatureBuildingMappingEntity buildingMapFeature = new BuildingFeatureBuildingMappingEntity();

        Optional<BuildingFeatureEntity> featureResult = this.buildingFeatureRepository.findById(featureID);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureID);
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
          .body("Building created, but the following feature IDs were not found: " + invalidFeatures);
    }

    String response = "Building was added succesfully! Building ID: " + savedBuilding.getId().toString();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}