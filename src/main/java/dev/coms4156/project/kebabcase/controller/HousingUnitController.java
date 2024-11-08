package dev.coms4156.project.kebabcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureHousingUnitMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;
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

/**
 * REST controller for managing housing units and associated features within buildings.
 * <p>
 * Provides endpoints to retrieve, create, and update housing units, and to manage
 * their associations with features. Supports partial updates, conflict handling,
 * and error responses when entities or features are not found.
 * </p>
 * 
 * <h2>Endpoints:</h2>
 * <ul>
 *   <li><strong>GET /building/{id}/housing-units</strong>: Retrieve housing units for 
 *        a building.</li>
 *   <li><strong>GET /housing-unit/{id}</strong>: Retrieve details of a specific 
 *        housing unit.</li>
 *   <li><strong>POST /housing-unit</strong>: Create a new housing unit within 
 *        a building.</li>
 *   <li><strong>PATCH /housing-unit/{id}</strong>: Update an existing housing unit.</li>
 * </ul>
 * 
 * <h2>Error Handling:</h2>
 * <p>
 * - HTTP 404: Building or housing unit not found.<br>
 * - HTTP 400: No fields provided for update.<br>
 * - HTTP 409: Housing unit with the same unit number already exists.<br>
 * - HTTP 206: Some feature IDs are invalid.
 * </p>
 */
@RestController
public class HousingUnitController {
  private final HousingUnitRepositoryInterface housingUnitRepository;
  private final BuildingRepositoryInterface buildingRepository;
  private final BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository;
  private final HousingUnitFeatureRepositoryInterface unitFeatureRepository;
  private final HousingUnitFeatureHousingUnitMappingRepositoryInterface 
                  unitFeatureMappingRepository;
  private final ObjectMapper objectMapper;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  /**
   * Constructs a new {@link HousingUnitController} to manage housing units and their 
   * associated features.
   * <p>
   * This constructor initializes the repositories and object mapper needed for operations
   * involving housing units, buildings, and their features.
   * </p>
   *
   * @param housingUnitRepository the repository for housing unit entities
   * @param buildingRepository the repository for building entities
   * @param unitFeatureRepository the repository for housing unit feature entities
   * @param unitFeatureMappingRepository the repository for mapping housing units to features
   * @param objectMapper the object mapper used for creating JSON objects in response bodies
   */
  public HousingUnitController(
      HousingUnitRepositoryInterface housingUnitRepository,
      BuildingRepositoryInterface buildingRepository,
      BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository,
      HousingUnitFeatureRepositoryInterface unitFeatureRepository,
      HousingUnitFeatureHousingUnitMappingRepositoryInterface unitFeatureMappingRepository,
      ObjectMapper objectMapper
  ) {
    this.housingUnitRepository = housingUnitRepository;
    this.buildingRepository = buildingRepository;
    this.buildingFeatureMappingRepository = buildingFeatureMappingRepository;
    this.unitFeatureRepository = unitFeatureRepository;
    this.unitFeatureMappingRepository = unitFeatureMappingRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * Retrieves a list of housing units for a specific building.
   * 
   * <p>Given a building ID, this method returns all housing units associated with the building.
   * If the building is not found, a 404 Not Found status is returned.
   * </p>
   *
   *
   * @param id the ID of the building to retrieve housing units for
   * @return a list of {@link ObjectNode} containing housing unit details
   * @throws ResponseStatusException if the building with the given ID is not found
   */
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
      ObjectNode json = this.objectMapper.createObjectNode();
      json.put("id", housingUnit.getId());
      json.put("unit_number", housingUnit.getUnitNumber());
      return json;
    }).collect(Collectors.toList());
  }

  /**
   * Retrieves a specific housing unit by its ID.
   * 
   * <p>This method fetches a housing unit based on its ID and returns its details as a JSON object.
   * If the unit is not found, a 404 Not Found response is returned.
   * </p>
   *
   *
   * @param id the ID of the housing unit to retrieve
   * @return a {@link ResponseEntity} containing the housing unit's details in JSON format, or 
   *         a 404 Not Found response if the unit is not found
   */
  @GetMapping("/housing-unit/{id}")
  public ResponseEntity<?> getHousingUnit(@PathVariable int id) {
    
    Optional<HousingUnitEntity> housingUnitRepoResult = this.housingUnitRepository.findById(id);

    if (housingUnitRepoResult.isEmpty()) {
      String errorMessage = "Housing unit with id " + id + " not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    HousingUnitEntity unit = housingUnitRepoResult.get();

    // Initialize JSON structure for housing unit
    ObjectNode json = this.objectMapper.createObjectNode();
    if (json == null) {
      throw new IllegalStateException("ObjectMapper could not create ObjectNode");
    }
    
    json.put("id", unit.getId());
    json.put("unit_number", unit.getUnitNumber());
    json.put("created_datetime", unit.getCreatedDatetime().format(formatter));
    json.put("modified_datetime", unit.getModifiedDatetime().format(formatter));

    // Retrieve building and add building information
    final BuildingEntity building = unit.getBuilding();
    ObjectNode buildingJson = json.putObject("building");
    buildingJson.put("id", building.getId());
    buildingJson.put("address", building.getAddress());
    buildingJson.put("city", building.getCity());
    buildingJson.put("state", building.getState());
    buildingJson.put("zip_code", building.getZipCode());

    // Add building features
    ArrayNode buildingFeaturesJson = buildingJson.putArray("features");
    List<BuildingFeatureBuildingMappingEntity> buildingFeatures = 
        this.buildingFeatureMappingRepository.findByBuilding(building);

    for (BuildingFeatureBuildingMappingEntity featureMapping : buildingFeatures) {
      buildingFeaturesJson.add(featureMapping.getBuildingFeature().getName());
    }

    // Add housing unit features
    ArrayNode housingUnitFeaturesJson = json.putArray("housing_unit_features");
    List<HousingUnitFeatureHousingUnitMappingEntity> unitFeatures = 
        this.unitFeatureMappingRepository.findByHousingUnit(unit);

    for (HousingUnitFeatureHousingUnitMappingEntity featureMapping : unitFeatures) {
      housingUnitFeaturesJson.add(featureMapping.getHousingUnitFeature().getName());
    }

    return ResponseEntity.ok(json);
  }

  /**
   * Updates the information of an existing housing unit by its ID.
   * 
   * <p>Updates the unit number, adds or removes features, and updates the modified date. 
   * If no fields (unitNumber, addFeatures, or removeFeatures) are provided, an HTTP 400 
   * Bad Request is returned.
   * If features are added or removed, valid feature IDs will be processed and invalid ones will
   * return HTTP 206 Partial Content with a list of invalid feature IDs.
   * </p>
   *
   *
   * @param id the ID of the housing unit to update
   * @param unitNumber the new unit number for the housing unit (optional)
   * @param addFeatures a list of feature IDs to add to the housing unit (optional)
   * @param removeFeatures a list of feature IDs to remove from the housing unit (optional)
   * @return a {@link ResponseEntity} indicating the result of the update, or errors if the 
   *     update fails
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
        String errorMessage = "Conflict: Feature IDs present in both add and remove lists: " 
                                + addFeaturesSet;
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
      for (Integer featureId : addFeatures) {
        HousingUnitFeatureHousingUnitMappingEntity unitMapFeature = 
            new HousingUnitFeatureHousingUnitMappingEntity();

        Optional<HousingUnitFeatureEntity> featureResult = 
            this.unitFeatureRepository.findById(featureId);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureId);
        } else {
          HousingUnitFeatureEntity feature = featureResult.get();

          Optional<HousingUnitFeatureHousingUnitMappingEntity> existingMapping =
              this.unitFeatureMappingRepository
                  .findByHousingUnitAndHousingUnitFeature(unit, feature);

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
      for (Integer featureId : removeFeatures) {
        Optional<HousingUnitFeatureEntity> featureResult = 
            this.unitFeatureRepository.findById(featureId);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureId);
        } else {
          HousingUnitFeatureEntity feature = featureResult.get();

          Optional<HousingUnitFeatureHousingUnitMappingEntity> existingMapping =
              this.unitFeatureMappingRepository
                  .findByHousingUnitAndHousingUnitFeature(unit, feature);

          if (existingMapping.isPresent()) {
            unitFeatureMappingRepository.delete(existingMapping.get());
          }
        }
      }
    }

    if (unitNumber == null
        && (addFeatures != null || removeFeatures != null)
        && ((addFeatures == null || invalidFeatures.containsAll(addFeatures))
          && (removeFeatures == null || invalidFeatures.containsAll(removeFeatures)))) {
      String errorMessage = "Could not find any of the housing unit features requested.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    if (!invalidFeatures.isEmpty()) {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body("Housing unit updated, but the following feature IDs were not found: " 
                  + invalidFeatures);
    }

    return ResponseEntity.ok("Housing unit info has been successfully updated!");
  }

  /**
   * Creates a new housing unit and associates it with an existing building.
   * 
   * <p>The new housing unit will have the specified unit number and will be 
   * linked to the given building. 
   * Optionally, features can be associated with the new housing unit.
   * If the building is not found, a 404 Not Found response is returned. If a housing unit with the
   * same unit number already exists within the building, a 409 Conflict response is returned.
   * </p>
   *
   *
   * @param buildingId the ID of the building to associate the new housing unit with
   * @param unitNumber the unit number for the new housing unit
   * @param features a list of feature IDs to associate with the housing unit (optional)
   * @return a {@link ResponseEntity} containing the new housing unit's ID, or error messages if 
   *         the building or features are not found
   */
  @PostMapping("/housing-unit")
  public ResponseEntity<?> createBuilding(
      @RequestParam int buildingId,
      @RequestParam String unitNumber,
      @RequestParam(required = false) List<Integer> features
  ) {

    /* Check if building exists */
    Optional<BuildingEntity> buildingRepoResult = this.buildingRepository.findById(buildingId);

    if (buildingRepoResult.isEmpty()) {
      String errorMessage = "Building with id " + buildingId + " not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    BuildingEntity building = buildingRepoResult.get();

    /* Check if the housing unit already exists */
    Optional<HousingUnitEntity> existingUnit = 
        housingUnitRepository.findByBuildingAndUnitNumber(building, unitNumber);

    if (existingUnit.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("A housing unit in the same building already exists.");
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
      for (Integer featureId : features) {
        HousingUnitFeatureHousingUnitMappingEntity housingUnitMapFeature = 
            new HousingUnitFeatureHousingUnitMappingEntity();

        Optional<HousingUnitFeatureEntity> featureResult = 
            this.unitFeatureRepository.findById(featureId);

        if (featureResult.isEmpty()) {
          invalidFeatures.add(featureId);
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
          .body("Building created, but the following feature IDs were not found: " 
                  + invalidFeatures);
    }

    String response = "Housing Unit was added succesfully! Housing Unit ID: " 
                        + savedUnit.getId().toString();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}
