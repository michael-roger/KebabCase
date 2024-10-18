package dev.coms4156.project.kebabcase.controller;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureHousingUnitMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;

/** TODO */
@RestController
public class HousingUnitController {
  private final HousingUnitRepositoryInterface housingUnitRepository;

  private final BuildingRepositoryInterface buildingRepository;

  private final HousingUnitFeatureRepositoryInterface unitFeatureRepository;

  private final HousingUnitFeatureHousingUnitMappingRepositoryInterface unitFeatureMappingRepository;

  private final ObjectMapper objectMapper;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


  public HousingUnitController(
      HousingUnitRepositoryInterface housingUnitRepository,
      BuildingRepositoryInterface buildingRepository,
      HousingUnitFeatureRepositoryInterface unitFeatureRepository,
      HousingUnitFeatureHousingUnitMappingRepositoryInterface unitFeatureMappingRepository,
      ObjectMapper objectMapper
  ) {
    this.housingUnitRepository = housingUnitRepository;
    this.buildingRepository = buildingRepository;
    this.unitFeatureRepository = unitFeatureRepository;
    this.unitFeatureMappingRepository = unitFeatureMappingRepository;
    this.objectMapper = objectMapper;
  }

  @GetMapping("/building/{id}/housing-units")
  public List<ObjectNode> getBuildingHousingUnits(@PathVariable int id) {

    Optional<BuildingEntity> buildingRepositoryResult = this.buildingRepository.findById(id);

    if (buildingRepositoryResult.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Building with id " + id + " not found"
      );
    }

    BuildingEntity building = buildingRepositoryResult.get();

    List<HousingUnitEntity>  housingUnits =
        this.housingUnitRepository.findByBuilding(building);

    return housingUnits.stream().map(housingUnit -> {
      ObjectNode Json = this.objectMapper.createObjectNode();
      Json.put("id", housingUnit.getId());
      Json.put("unit_number", housingUnit.getUnitNumber());
      return Json;
    }).collect(Collectors.toList());
  }

  /**
   * Retrieves a housing unit by its ID and returns its details as a JSON object.
   * <p>
   * This method uses the housing unit ID to search the repository for a corresponding
   * {@link HousingUnitEntity}. If found, the housing unit's details such as ID, building ID,
   * unit number, created date, and modified date are returned in JSON format. If the housing unit 
   * is not found, an HTTP 404 Not Found status is returned.
   * </p>
   *
   * @param id the ID of the housing unit to retrieve
   * @return an {@link ObjectNode} containing the housing unit's details
   * @throws ResponseStatusException if the housing unit with the given ID is not found
   */
  @GetMapping("/housing-unit/{id}")
  public ResponseEntity<?> getHousingUnit(@PathVariable int id) {
    
    Optional<HousingUnitEntity> housingUnitRepoResult = this.housingUnitRepository.findById(id);

    if (housingUnitRepoResult.isEmpty()) {
      String errorMessage = "Housing unit with id " + id + " not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    HousingUnitEntity unit = housingUnitRepoResult.get();

    ObjectNode json = this.objectMapper.createObjectNode();
    json.put("id", unit.getId());
    json.put("building_id", unit.getBuilding().getId());
    json.put("unit_number", unit.getUnitNumber());
    json.put("created_datetime", unit.getCreatedDatetime().format(formatter));
    json.put("modified_datetime", unit.getModifiedDatetime().format(formatter));

    return ResponseEntity.ok(json);
  }

  /**
   * Updates the information of an existing housing unit by its ID.
   * Specifically, it updates the unit number, the modified date, and optionally associates new features with the housing unit.
   * 
   * If no fields (unitNumber or features) are provided, an HTTP 400 Bad Request will be returned.
   * If features are provided, valid features will be added to the housing unit. 
   * If any feature ID in the list is not found, the update will still proceed, but an HTTP 206 Partial Content
   * will be returned with a message listing the invalid feature IDs.
   * 
   * @param id the ID of the housing unit to update
   * @param unitNumber the new unit number for the housing unit (optional)
   * @param features a list of feature IDs to associate with the housing unit (optional)
   * @return a {@link ResponseEntity} indicating the result of the update. If all updates succeed, 
   * an HTTP 200 OK is returned. If some feature IDs are invalid, an HTTP 206 Partial Content is returned 
   * with a message listing the invalid feature IDs.
   * @throws ResponseStatusException if the housing unit with the given ID is not found, or if no fields are provided for update
   */
  @PatchMapping("/housing-unit/{id}")
  public ResponseEntity<?> updateBuilding(
      @PathVariable int id, 
      @RequestParam(required = false) String unitNumber,
      @RequestParam(required = false) List<Integer> addFeatures,
      @RequestParam(required = false) List<Integer> removeFeatures
  ) {

    /* Check if unit exists */
    Optional<HousingUnitEntity> unitResult = housingUnitRepository.findById(id);

    if (unitResult.isEmpty()) {
      String errorMessage = "Building not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    HousingUnitEntity unit = unitResult.get();

    /* Check if the user entered anything to update */
    if (unitNumber == null && addFeatures == null && removeFeatures == null) {
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
    if (unitNumber != null) {
      unit.setUnitNumber(unitNumber);
    }

    unit.setModifiedDatetime(OffsetDateTime.now());

    housingUnitRepository.save(unit);

    Set<Integer> invalidFeatures = new HashSet<>();

    /* Add new unit feature to unit-feature mapping if exists */
    if (addFeatures != null) {
      for (Integer featureID : addFeatures) {
        HousingUnitFeatureHousingUnitMappingEntity unitMapFeature = new HousingUnitFeatureHousingUnitMappingEntity();

        Optional<HousingUnitFeatureEntity> featureResult = this.unitFeatureRepository.findById(featureID);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureID);
        } else {
          HousingUnitFeatureEntity feature = featureResult.get();

          Optional<HousingUnitFeatureHousingUnitMappingEntity> existingMapping =
            this.unitFeatureMappingRepository.findByHousingUnitAndHousingUnitFeature(unit, feature);

          if (existingMapping.isEmpty()) {
            unitMapFeature.setHousingUnit(unit);
            unitMapFeature.setHousingUnitFeature(feature);

            unitFeatureMappingRepository.save(unitMapFeature);
          }
        }
      }
    }

    /* Remove features */
    if (removeFeatures != null) {
      for (Integer featureID : removeFeatures) {
        Optional<HousingUnitFeatureEntity> featureResult = this.unitFeatureRepository.findById(featureID);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureID);
        } else {
          HousingUnitFeatureEntity feature = featureResult.get();

          Optional<HousingUnitFeatureHousingUnitMappingEntity> existingMapping =
              this.unitFeatureMappingRepository.findByHousingUnitAndHousingUnitFeature(unit, feature);

          if (existingMapping.isPresent()) {
            unitFeatureMappingRepository.delete(existingMapping.get());
          }
        }
      }
    }

    if (unitNumber == null &&
        addFeatures != null &&
        removeFeatures != null &&
        invalidFeatures.size() == (addFeatures.size() + removeFeatures.size())) {
      String errorMessage = "Could not find any of the housing unit features requested.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    if (!invalidFeatures.isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body("Housing unit updated, but the following feature IDs were not found: " + invalidFeatures);
    }

    return ResponseEntity.ok("Housing unit info has been successfully updated!");
  }

  /**
   * Creates a new housing unit and associates it with an existing building.
   * The new housing unit will have the specified unit number and be linked to the building
   * with the provided building ID.
   * 
   * @param buildingID the ID of the building to associate the new housing unit with
   * @param unitNumber the unit number for the new housing unit
   * @return a {@link ResponseEntity} containing a message and the new housing unit's ID
   * @throws ResponseStatusException if the building with the provided ID is not found
   */
  @PostMapping("/housing-unit")
  public ResponseEntity<?> createBuilding(
      @RequestParam int buildingID,
      @RequestParam String unitNumber,
      @RequestParam(required = false) List<Integer> features
  ) {

    /* Check if building exists */
    Optional<BuildingEntity> buildingRepoResult = this.buildingRepository.findById(buildingID);

    if (buildingRepoResult.isEmpty()) {
      String errorMessage = "Building with id " + buildingID + " not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    BuildingEntity building = buildingRepoResult.get();

    /* Check if the housing unit already exists */
    Optional<HousingUnitEntity> existingUnit = housingUnitRepository.findByBuildingAndUnitNumber(building, unitNumber);

    if (existingUnit.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("A housing unit in the same building already exists.");
    }

    /* Create a new housing unit */
    HousingUnitEntity newUnit = new HousingUnitEntity();

    newUnit.setBuilding(building);
    newUnit.setUnitNumber(unitNumber);
    newUnit.setCreatedDatetime(OffsetDateTime.now());
    newUnit.setModifiedDatetime(OffsetDateTime.now());

    HousingUnitEntity savedUnit = housingUnitRepository.save(newUnit);

    /* Add Housing Unit Features */
    List<Integer> invalidFeatures = new ArrayList<>();

    if (features != null) {
      for (Integer featureID : features) {
        HousingUnitFeatureHousingUnitMappingEntity housingUnitMapFeature = new HousingUnitFeatureHousingUnitMappingEntity();

        Optional<HousingUnitFeatureEntity> featureResult = this.unitFeatureRepository.findById(featureID);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureID);
        } else {
          HousingUnitFeatureEntity feature = featureResult.get();

          housingUnitMapFeature.setHousingUnit(savedUnit);
          housingUnitMapFeature.setHousingUnitFeature(feature);

          unitFeatureMappingRepository.save(housingUnitMapFeature);
        }
      }
    }

    if (!invalidFeatures.isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body("Building created, but the following feature IDs were not found: " + invalidFeatures);
    }

    String response = "Housing Unit was added succesfully! Housing Unit ID: " + savedUnit.getId().toString();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}
