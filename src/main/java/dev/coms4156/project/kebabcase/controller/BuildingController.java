package dev.coms4156.project.kebabcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.entity.BuildingUserMappingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
  private final BuildingUserMappingRepositoryInterface buildingUserMappingRepository;
  private final UserRepositoryInterface userRepository;
  private final ObjectMapper objectMapper;

  /**
   * Constructs a new BuildingController.
   *
   * @param buildingRepository the repository used to interact with building entities
   * @param buildingFeatureRepository the repository used for building features
   * @param buildingFeatureMappingRepository the repository for mapping building features
   * @param buildingUserMappingRepository the repository for mapping users to buildings
   * @param userRepository the repository used to interact with user entities
   */
  public BuildingController(
      BuildingRepositoryInterface buildingRepository,
      BuildingFeatureRepositoryInterface buildingFeatureRepository,
      BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository,
      BuildingUserMappingRepositoryInterface buildingUserMappingRepository,
      UserRepositoryInterface userRepository,
      ObjectMapper objectMapper
  ) {
    this.buildingRepository = buildingRepository;
    this.buildingFeatureRepository = buildingFeatureRepository;
    this.buildingFeatureMappingRepository = buildingFeatureMappingRepository;
    this.buildingUserMappingRepository = buildingUserMappingRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
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
        && (addFeatures != null || removeFeatures != null)
        && (addFeatures == null || invalidFeatures.containsAll(addFeatures))
        && (removeFeatures == null || invalidFeatures.containsAll(removeFeatures))) {
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

  /**
   * Displays a list of all buildings with the desired building feature.
   *
   * @param id the ID of the desired building feature
   * @return a list of {@link ObjectNode} containing the building
   *     information with the desired building feature
   * @throws ResponseStatusException if the building feature ID is not found
   **/

  @GetMapping("/building-feature/{id}/buildings")
  public List<ObjectNode> getBuildingHousingUnits(@PathVariable int id) {

    Optional<BuildingFeatureEntity> feature = this.buildingFeatureRepository.findById(id);
    if (feature.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Building with feature id " + id + " not found"
      );
    }

    List<BuildingFeatureBuildingMappingEntity> result =
        this.buildingFeatureMappingRepository.findByBuildingFeatureId(id);

    return result.stream().map(mapping -> {
      BuildingEntity building = mapping.getBuilding();
      ObjectNode json = objectMapper.createObjectNode();
      json.put("id", building.getId());
      json.put("building_address", building.getAddress());
      json.put("city", building.getCity());
      json.put("state", building.getState());
      json.put("zipcode", building.getZipCode());
      return json;
    }).collect(Collectors.toList());
  }

  /**
   * Helper method to create the base JSON structure for a building entity.
   *
   * @param building the building entity to convert to JSON
   * @return an {@link ObjectNode} containing the building's base information
   */
  private ObjectNode createBuildingJson(BuildingEntity building) {
    ObjectNode json = objectMapper.createObjectNode();
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
   * Retrieves detailed information for a specific building by its ID, including its address,
   * creation and modification dates, and associated building features.
   *
   * @param id the ID of the building to retrieve
   * @return a {@link ResponseEntity} containing the building details in JSON format or
   *         a 404 Not Found response if the building is not found
   */
  @GetMapping("/building/{id}")
  public ResponseEntity<?> getBuildingById(@PathVariable int id) {

    Optional<BuildingEntity> buildingRepositoryResult = this.buildingRepository.findById(id);
    if (buildingRepositoryResult.isEmpty()) {
      String errorMessage = "Building with id " + id + " not found.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    BuildingEntity building = buildingRepositoryResult.get();
    ObjectNode json = createBuildingJson(building);

    // Add building features
    ArrayNode buildingFeaturesJson = json.putArray("features");
    List<BuildingFeatureBuildingMappingEntity> buildingFeatures = 
        this.buildingFeatureMappingRepository.findByBuilding(building);

    for (BuildingFeatureBuildingMappingEntity featureMapping : buildingFeatures) {
      buildingFeaturesJson.add(featureMapping.getBuildingFeature().getName());
    }

    return ResponseEntity.ok(json);
  }

  /**
   * Retrieves a list of buildings associated with a specific user by their user ID, 
   * including building addresses and features.
   *
   * @param id the ID of the user whose associated buildings are to be retrieved
   * @return a {@link ResponseEntity} containing a list of buildings in JSON format, or
   *         a 404 Not Found response if the user is not found
   */
  @GetMapping("/user/{id}/buildings")
  public ResponseEntity<?> getUserBuildings(@PathVariable int id) {

    Optional<UserEntity> user = this.userRepository.findById(id);
    if (user.isEmpty()) {
      String errorMessage = "User with id " + id + " not found.";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    List<BuildingUserMappingEntity> result = this.buildingUserMappingRepository.findByUserId(id);
    List<ObjectNode> buildings = result.stream().map(mapping -> {
      BuildingEntity building = mapping.getBuilding();
      
      // Create JSON for building information and add features
      ObjectNode json = createBuildingJson(building);
      ArrayNode buildingFeaturesJson = json.putArray("features");

      List<BuildingFeatureBuildingMappingEntity> buildingFeatures = 
          this.buildingFeatureMappingRepository.findByBuilding(building);

      for (BuildingFeatureBuildingMappingEntity featureMapping : buildingFeatures) {
        buildingFeaturesJson.add(featureMapping.getBuildingFeature().getName());
      }

      return json;
    }).collect(Collectors.toList());

    return ResponseEntity.ok(buildings);
  }

  /**
   * Adds an existing building to an existing user by creating a new entry in the 
   * `building_user_mappings` table. If the user or building does not exist, returns
   * a 404 Not Found status. If the building is already linked to the user, returns 
   * a 409 Conflict status.
   *
   * @param userId The ID of the user to whom the building will be linked.
   * @param buildingId The ID of the building to be linked to the user.
   * @return ResponseEntity containing a JSON response with the linkage status. 
   *         Returns a 201 Created status if successful, 404 Not Found if the user
   *         or building does not exist, and 409 Conflict if the link already exists.
   */
  @PostMapping("/user/{userId}/buildings/{buildingId}")
  public ResponseEntity<?> addExistingBuildingToUser(@PathVariable int userId, 
                                                      @PathVariable int buildingId) {
    // Check if the user exists
    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("User with id " + userId + " not found.");
    }
    UserEntity user = userOpt.get();

    // Check if the building exists
    Optional<BuildingEntity> buildingOpt = buildingRepository.findById(buildingId);
    if (buildingOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Building with id " + buildingId + " not found.");
    }
    BuildingEntity building = buildingOpt.get();

    // Check if the mapping already exists
    Optional<BuildingUserMappingEntity> existingMapping = 
        buildingUserMappingRepository.findByUserIdAndBuildingId(userId, buildingId);

    if (existingMapping.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("This building is already linked to the user.");
    }

    // Create and save the mapping
    BuildingUserMappingEntity mapping = new BuildingUserMappingEntity();
    mapping.setUser(user);
    mapping.setBuilding(building);
    mapping.setCreatedDatetime(OffsetDateTime.now());
    mapping.setModifiedDatetime(OffsetDateTime.now());

    buildingUserMappingRepository.save(mapping);

    // Return a success response
    ObjectNode responseJson = objectMapper.createObjectNode();
    responseJson.put("user_id", userId);
    responseJson.put("building_id", buildingId);
    responseJson.put("status", "Building successfully linked to user.");

    return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
  }
  
  /**
   * Removes the association between a user and a building.
   *
   * @param userId the ID of the user.
   * @param buildingId the ID of the building.
   * @return a {@link ResponseEntity} containing the status of the operation.
   *         <ul>
   *           <li>HTTP 200: Association removed successfully.</li>
   *           <li>HTTP 404: User, building, or association not found.</li>
   *         </ul>
   */
  @DeleteMapping("/user/{userId}/building/{buildingId}")
  public ResponseEntity<?> removeBuildingFromUser(@PathVariable int userId, 
                                                @PathVariable int buildingId) {
    // Check if the user exists
    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("User with id " + userId + " not found.");
    }

    // Check if the building exists
    Optional<BuildingEntity> buildingOpt = buildingRepository.findById(buildingId);
    if (buildingOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Building with id " + buildingId + " not found.");
    }

    // Check if the mapping exists
    Optional<BuildingUserMappingEntity> mappingOpt = 
        buildingUserMappingRepository.findByUserIdAndBuildingId(userId, buildingId);

    if (mappingOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("This building is not linked to the user.");
    }

    // Remove the mapping
    buildingUserMappingRepository.delete(mappingOpt.get());

    // Return a success response
    ObjectNode responseJson = objectMapper.createObjectNode();
    responseJson.put("user_id", userId);
    responseJson.put("building_id", buildingId);
    responseJson.put("status", "Building successfully unlinked from user.");

    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  /**
   * Retrieves a list of all buildings from the repository,
   * returns all buildings in the repository as a list. 
   *
   * @param  address an optional request parameter to select buildings containing
   *         the specified address.
   * @param city an optional request parameter to select buildings with the specified
   *         city.
   * @return ResponseEntity containing the list of buildings as a JSON response.
   *         Returns a 200 OK status if buildings are found, or 204 No Content if no 
   *         buildings exist in the repository.
   */
  @GetMapping("/buildings")
  public ResponseEntity<List<BuildingEntity>> getBuildings(
           @RequestParam(required = false) String address,
           @RequestParam(required = false) String city,
           @RequestParam(required = false) String state) {

    // filter by address
    if (address != null && !address.isEmpty()) {
      Optional<BuildingEntity> building = buildingRepository.findByAddress(address);
      if (building.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
      }
      return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(building.get()));
    }

    // filter by city
    if (city != null && !city.isEmpty()) {
      List<BuildingEntity> buildings = buildingRepository.findByCity(city);
      if (buildings.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
      }
      return ResponseEntity.status(HttpStatus.OK).body(buildings);
    }

    // filter by state
    if (state != null && !state.isEmpty()) {
      List<BuildingEntity> buildings = buildingRepository.findByState(state);
      if (buildings.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
      }
      return ResponseEntity.status(HttpStatus.OK).body(buildings);
    }

    List<BuildingEntity> buildings = buildingRepository.findAll();

    if (buildings.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(buildings);
  }

  /**
   * Retrieves all housing units associated with a specific building,
   * given a building ID, this method fetches the building and retrieves the housing units,
   * linked to it. 
   *
   * @param buildingId The ID of the building for which housing units are being fetched.
   * @return {@link ResponseEntity} containing:
   *           200 OK: If housing units are successfully retrieved and units.
   *           204 No Content: If the building has no associated housing units.
   *           404 Not Found: If the building does not exist.
   */
  @GetMapping("/buildings/{buildingId}/housing-units")
  public ResponseEntity<Set<HousingUnitEntity>> 
      getHousingUnitsByBuilding(@PathVariable Integer buildingId) {
    Optional<BuildingEntity> building = buildingRepository.findById(buildingId);
    
    if (building.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    Set<HousingUnitEntity> housingUnits = building.get().getHousingUnits();

    // Return NO_CONTENT if the building has no housing units
    if (housingUnits.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(housingUnits);
  }

}
