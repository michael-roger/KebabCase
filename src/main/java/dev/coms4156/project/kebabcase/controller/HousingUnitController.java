package dev.coms4156.project.kebabcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitUserMappingEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureHousingUnitMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final HousingUnitUserMappingRepositoryInterface unitUserMappingRepository;
  private final UserRepositoryInterface userRepository;
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
   * @param unitUserMappingRepository the repository for mapping users to housing units
   * @param objectMapper the object mapper used for creating JSON objects in response bodies
   */
  public HousingUnitController(
      HousingUnitRepositoryInterface housingUnitRepository,
      BuildingRepositoryInterface buildingRepository,
      BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository,
      HousingUnitFeatureRepositoryInterface unitFeatureRepository,
      HousingUnitFeatureHousingUnitMappingRepositoryInterface unitFeatureMappingRepository,
      HousingUnitUserMappingRepositoryInterface unitUserMappingRepository,
      UserRepositoryInterface userRepository,
      ObjectMapper objectMapper
  ) {
    this.housingUnitRepository = housingUnitRepository;
    this.buildingRepository = buildingRepository;
    this.buildingFeatureMappingRepository = buildingFeatureMappingRepository;
    this.unitFeatureRepository = unitFeatureRepository;
    this.unitFeatureMappingRepository = unitFeatureMappingRepository;
    this.unitUserMappingRepository = unitUserMappingRepository;
    this.userRepository = userRepository;
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
        && (addFeatures == null || invalidFeatures.containsAll(addFeatures))
        && (removeFeatures == null || invalidFeatures.containsAll(removeFeatures))) {
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

  /**
   * Helper method to create the base JSON structure for a housing unit entity.
   *
   * @param unit the housing unit entity to convert to JSON
   * @return an {@link ObjectNode} containing the housing unit's base information
   */
  private ObjectNode getHousingUnitInfo(HousingUnitEntity unit) {
    ObjectNode json = objectMapper.createObjectNode();
    json.put("id", unit.getId());
    json.put("unit_number", unit.getUnitNumber());
    json.put("created_datetime", unit.getCreatedDatetime().format(formatter));
    json.put("modified_datetime", unit.getModifiedDatetime().format(formatter));

    // Retrieve building and add building information
    BuildingEntity building = unit.getBuilding();
    ObjectNode buildingJson = json.putObject("building");
    buildingJson.put("id", building.getId());
    buildingJson.put("address", building.getAddress());
    buildingJson.put("city", building.getCity());
    buildingJson.put("state", building.getState());
    buildingJson.put("zip_code", building.getZipCode());

    // Add building features
    ArrayNode buildingFeaturesJson = buildingJson.putArray("features");
    List<BuildingFeatureBuildingMappingEntity> buildingFeatures = 
        buildingFeatureMappingRepository.findByBuilding(building);
    for (BuildingFeatureBuildingMappingEntity featureMapping : buildingFeatures) {
      buildingFeaturesJson.add(featureMapping.getBuildingFeature().getName());
    }

    // Add housing unit features
    ArrayNode housingUnitFeaturesJson = json.putArray("housing_unit_features");
    List<HousingUnitFeatureHousingUnitMappingEntity> unitFeatures = 
        unitFeatureMappingRepository.findByHousingUnit(unit);
    for (HousingUnitFeatureHousingUnitMappingEntity featureMapping : unitFeatures) {
      housingUnitFeaturesJson.add(featureMapping.getHousingUnitFeature().getName());
    }

    return json;
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
    Optional<HousingUnitEntity> housingUnitRepoResult = housingUnitRepository.findById(id);
    if (housingUnitRepoResult.isEmpty()) {
      String errorMessage = "Housing unit with id " + id + " not found";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    HousingUnitEntity unit = housingUnitRepoResult.get();
    ObjectNode json = getHousingUnitInfo(unit);
    return ResponseEntity.ok(json);
  }

  /**
   * Retrieves all housing units associated with a specific user by their user ID.
   *
   * <p>This endpoint fetches all housing units linked to the specified user ID by querying
   * the `HousingUnitUserMapping` entities and obtaining each corresponding housing unit's 
   * information. If the user does not exist, a 404 Not Found response is returned.
   * </p>
   *
   * @param id the ID of the user for whom to retrieve associated housing units
   * @return a {@link ResponseEntity} containing a list of {@link ObjectNode} JSON objects,
   *         each representing detailed information about a housing unit. If the user is not
   *         found, returns a 404 Not Found response with an error message.
   */
  @GetMapping("/user/{id}/housing-units")
  public ResponseEntity<?> getUserHousingUnits(@PathVariable int id) {
    Optional<UserEntity> user = userRepository.findById(id);
    if (user.isEmpty()) {
      String errorMessage = "User with id " + id + " not found.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    List<HousingUnitUserMappingEntity> result = unitUserMappingRepository.findByUserId(id);
    List<ObjectNode> housingUnits = result.stream()
        .map(mapping -> getHousingUnitInfo(mapping.getHousingUnit()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(housingUnits);
  }

  /**
   * Adds an existing unit to an existing user by creating a new entry in the 
   * `housing_unit_user_mappings` table. If the user or unit does not exist, returns
   * a 404 Not Found status. If the unnit is already linked to the user, returns 
   * a 409 Conflict status.
   *
   * @param userId The ID of the user to whom the building will be linked.
   * @param housingUnitId The ID of the housing unit to be linked to the user.
   * @return ResponseEntity containing a JSON response with the linkage status. 
   *         Returns a 201 Created status if successful, 404 Not Found if the user
   *         or unit does not exist, and 409 Conflict if the link already exists.
   */
  @PostMapping("/user/{userId}/housing-unit/{housingUnitId}")
  public ResponseEntity<?> addExistingUnitToUser(@PathVariable int userId, 
                                                  @PathVariable int housingUnitId) {
    // Check if the user exists
    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("User with id " + userId + " not found.");
    }
    UserEntity user = userOpt.get();

    // Check if the unit exists
    Optional<HousingUnitEntity> unitOpt = housingUnitRepository.findById(housingUnitId);
    if (unitOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Housing unit with id " + housingUnitId + " not found.");
    }
    HousingUnitEntity unit = unitOpt.get();

    // Check if the mapping already exists
    Optional<HousingUnitUserMappingEntity> existingMapping = 
        unitUserMappingRepository.findByUserIdAndHousingUnitId(userId, housingUnitId);

    if (existingMapping.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("This housing unit is already linked to the user.");
    }

    // Create and save the mapping
    HousingUnitUserMappingEntity mapping = new HousingUnitUserMappingEntity();
    mapping.setUser(user);
    mapping.setHousingUnit(unit);
    mapping.setCreatedDatetime(OffsetDateTime.now());
    mapping.setModifiedDatetime(OffsetDateTime.now());

    unitUserMappingRepository.save(mapping);

    // Return a success response
    ObjectNode responseJson = objectMapper.createObjectNode();
    responseJson.put("user_id", userId);
    responseJson.put("housing_unit_id", housingUnitId);
    responseJson.put("status", "Housing unit successfully linked to user.");

    return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
  }

  /**
   * Removes the association between a user and a housing unit.
   *
   * @param userId the ID of the user.
   * @param housingUnitId the ID of the housing unit.
   * @return a {@link ResponseEntity} containing the status of the operation.
   *         <ul>
   *           <li>HTTP 200: Association removed successfully.</li>
   *           <li>HTTP 404: User, housing unit, or association not found.</li>
   *         </ul>
   */
  @DeleteMapping("/user/{userId}/housing-unit/{housingUnitId}")
  public ResponseEntity<?> removeHousingUnitFromUser(@PathVariable int userId, 
                                                   @PathVariable int housingUnitId) {
    // Check if the user exists
    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("User with id " + userId + " not found.");
    }

    // Check if the housing unit exists
    Optional<HousingUnitEntity> housingUnitOpt = housingUnitRepository.findById(housingUnitId);
    if (housingUnitOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Housing unit with id " + housingUnitId + " not found.");
    }

    // Check if the mapping exists
    Optional<HousingUnitUserMappingEntity> mappingOpt = 
        unitUserMappingRepository.findByUserIdAndHousingUnitId(userId, housingUnitId);

    if (mappingOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("This housing unit is not linked to the user.");
    }

    // Remove the mapping
    unitUserMappingRepository.delete(mappingOpt.get());

    // Return a success response
    ObjectNode responseJson = objectMapper.createObjectNode();
    responseJson.put("user_id", userId);
    responseJson.put("housing_unit_id", housingUnitId);
    responseJson.put("status", "Housing unit successfully unlinked from user.");

    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

}
