package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.controller.BuildingController;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.entity.BuildingUserMappingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BuildingControllerUnitTests {

  @Mock
  private BuildingRepositoryInterface buildingRepository;

  @Mock
  private BuildingFeatureRepositoryInterface buildingFeatureRepository;

  @Mock
  private BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository;

  @Mock
  private BuildingUserMappingRepositoryInterface buildingUserMappingRepository;

  @Mock
  private UserRepositoryInterface userRepository;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private BuildingController buildingController;

  @BeforeEach
  void setUp() {
    // Initialize mocks and inject them into the controller
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetBuildingByIdSuccess() {
    // Arrange
    OffsetDateTime nowDateTime = OffsetDateTime.now();

    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setAddress("123 Test Street");
    building.setCity("Test City");
    building.setState("TS");
    building.setZipCode("12345");
    building.setCreatedDatetime(nowDateTime);
    building.setModifiedDatetime(nowDateTime);

    // Mock repository response
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Create the expected JSON response using ObjectMapper
    ObjectNode jsonNode = mock(ObjectNode.class);
    when(objectMapper.createObjectNode()).thenReturn(jsonNode);
    when(jsonNode.put(anyString(), anyString())).thenReturn(jsonNode);

    // Act
    ResponseEntity<?> result = buildingController.getBuildingById(1);

    // Assert
    assertNotNull(result); // Check that the result is not null
    // Verify that repository is called once
    verify(buildingRepository, times(1)).findById(1);
  }

  @Test
  void testGetBuildingByIdNotFound() {
    // Arrange
    when(buildingRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.getBuildingById(999);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Building with id 999 not found.", response.getBody());
    verify(buildingRepository, times(1)).findById(999);
  }

  @Test
  void testCreateBuildingSuccessWithoutFeatures() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setAddress("123 Test Street");
    building.setCity("Test City");
    building.setState("TS");
    building.setZipCode("12345");

    when(buildingRepository.findByAddressAndCityAndStateAndZipCode(
        "123 Test Street", "Test City", "TS", "12345"))
        .thenReturn(Optional.empty()); // No duplicate address

    when(buildingRepository.save(any(BuildingEntity.class))).thenReturn(building);

    // Act
    ResponseEntity<?> response = buildingController.createBuilding(
        "123 Test Street", "Test City", "TS", "12345", null);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building was added succesfully!"));
    verify(buildingRepository, times(1)).save(any(BuildingEntity.class));
    verify(buildingFeatureMappingRepository, times(0)).save(any(BuildingFeatureBuildingMappingEntity.class));
  }

  @Test
  void testCreateBuildingSuccessWithValidFeatures() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setAddress("123 Test Street");

    BuildingFeatureEntity feature1 = new BuildingFeatureEntity();
    BuildingFeatureEntity feature2 = new BuildingFeatureEntity();

    when(buildingRepository.findByAddressAndCityAndStateAndZipCode(
        "123 Test Street", "Test City", "TS", "12345"))
        .thenReturn(Optional.empty()); // No duplicate address

    when(buildingRepository.save(any(BuildingEntity.class))).thenReturn(building);
    when(buildingFeatureRepository.findById(1)).thenReturn(Optional.of(feature1));
    when(buildingFeatureRepository.findById(2)).thenReturn(Optional.of(feature2));

    // Act
    ResponseEntity<?> response = buildingController.createBuilding(
        "123 Test Street", "Test City", "TS", "12345", List.of(1, 2));

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building was added succesfully!"));
    verify(buildingRepository, times(1)).save(any(BuildingEntity.class));
    verify(buildingFeatureMappingRepository, times(2)).save(any(BuildingFeatureBuildingMappingEntity.class));
  }

  @Test
  void testCreateBuildingPartialContentWithInvalidFeatures() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setAddress("123 Test Street");

    BuildingFeatureEntity validFeature = new BuildingFeatureEntity();
    validFeature.setId(1);

    when(buildingRepository.findByAddressAndCityAndStateAndZipCode(
        "123 Test Street", "Test City", "TS", "12345"))
        .thenReturn(Optional.empty()); // No duplicate address

    when(buildingRepository.save(any(BuildingEntity.class))).thenReturn(building);
    when(buildingFeatureRepository.findById(1)).thenReturn(Optional.of(validFeature)); // Valid feature
    when(buildingFeatureRepository.findById(-1)).thenReturn(Optional.empty()); // Invalid feature

    // Act
    ResponseEntity<?> response = buildingController.createBuilding(
        "123 Test Street", "Test City", "TS", "12345", List.of(1, -1));

    // Assert
    assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building created, but the following feature IDs were not found: [-1]"));
    verify(buildingRepository, times(1)).save(any(BuildingEntity.class));
    verify(buildingFeatureMappingRepository, times(1)).save(any(BuildingFeatureBuildingMappingEntity.class));
  }

  @Test
  void testCreateBuildingConflictDuplicateAddress() {
    // Arrange
    BuildingEntity existingBuilding = new BuildingEntity();
    existingBuilding.setId(1);
    existingBuilding.setAddress("123 Test Street");

    when(buildingRepository.findByAddressAndCityAndStateAndZipCode(
        "123 Test Street", "Test City", "TS", "12345"))
        .thenReturn(Optional.of(existingBuilding)); // Duplicate address found

    // Act
    ResponseEntity<?> response = buildingController.createBuilding(
        "123 Test Street", "Test City", "TS", "12345", null);

    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("A building with the same address already exists."));
    verify(buildingRepository, times(0)).save(any(BuildingEntity.class));
    verify(buildingFeatureMappingRepository, times(0)).save(any(BuildingFeatureBuildingMappingEntity.class));
  }

  @Test
  void testUpdateBuildingSuccess() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setAddress("123 Test Street");

    when(buildingRepository.findById(1)).thenReturn(Optional.of(building)); // Mock building found
    when(buildingRepository.save(any(BuildingEntity.class))).thenReturn(building); // Mock save

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
        1, "456 New Street", null, null,
        null, null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().toString()
        .contains("Building info has been successfully updated!"));
    verify(buildingRepository, times(1))
        .save(any(BuildingEntity.class));
  }

  @Test
  void testUpdateBuildingNotFound() {
    // Arrange
    when(buildingRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
        999, "123 New Street", null, null,
        null, null, null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building not found"));
  }

  @Test
  void testUpdateBuildingNoFieldsProvided() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
        1, null, null, null, null, null, null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("No fields provided for update"));
  }

  @Test
  void testUpdateBuildingConflictInAddAndRemoveFeatures() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));
    List<Integer> addFeatures = List.of(1, 2);
    List<Integer> removeFeatures = List.of(2, 3); // Conflict on feature ID 2

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
        1, null, null, null, null, addFeatures, removeFeatures);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Conflict: Feature IDs present in both add and remove lists"));
  }

  @Test
  void testUpdateBuildingPartialContentInvalidFeatures() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));
    List<Integer> addFeatures = List.of(1, -1);  // Assume feature ID -1 is invalid

    when(buildingFeatureRepository.findById(1)).thenReturn(Optional.of(new BuildingFeatureEntity()));
    when(buildingFeatureRepository.findById(-1)).thenReturn(Optional.empty()); // Feature -1 not found

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
        1, "New Address", null, null, null, addFeatures, null);

    // Assert
    assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building updated, but the following feature IDs were not found: [-1]"));
  }

  @Test
  void testUpdateBuildingAllInvalidFeatures() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    List<Integer> addFeatures = List.of(-1, -2); // Assume all IDs are invalid

    when(buildingFeatureRepository.findById(-1)).thenReturn(Optional.empty());
    when(buildingFeatureRepository.findById(-2)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
        1, null, null, null, null, addFeatures, null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Could not find any of the building features requested."));
  }

  @Test
  void testUpdateBuildingdCityOnly() {
  BuildingEntity building = new BuildingEntity();
  building.setId(1);
  when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

  // Act
  ResponseEntity<?> response = buildingController.updateBuilding(
            1, null, "someCity", null, null, null, null);

  // Assert
  assertEquals(HttpStatus.OK, response.getStatusCode());
  assertEquals("someCity", building.getCity());
  assertEquals("Building info has been successfully updated!", response.getBody());
  verify(buildingRepository, times(1))
            .save(building);
  }

  @Test
  void testUpdateBuildingStateOnly() {
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
            1, null, null, "MA", null, null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Building info has been successfully updated!", response.getBody());
    assertEquals("MA", building.getState());
    verify(buildingRepository, times(1))
            .save(building);
  }

  @Test
  void testUpdateBuildingZipCodeOnly() {
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
            1, null, null, null, "02113", null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Building info has been successfully updated!", response.getBody());
    assertEquals("02113", building.getZipCode());
    verify(buildingRepository, times(1))
            .save(building);
  }

  @Test
  void testUpdateBuildingCityAndStateOnly() {
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Act
    ResponseEntity<?> response = buildingController.updateBuilding(
            1, null, "nyc", "NY", null, null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Building info has been successfully updated!", response.getBody());
    verify(buildingRepository, times(1))
            .save(any(BuildingEntity.class));
  }

  @Test
  void testGetUserBuildings_UserNotFound() {
    // Arrange
    int userId = 999;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.getUserBuildings(userId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User with id " + userId + " not found.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(buildingUserMappingRepository);
    verifyNoInteractions(buildingFeatureMappingRepository);
  }

  @Test
  void testGetUserBuildings_SuccessWithExistingUser() {
    // Arrange
    int userId = 1;

    // Mock UserEntity
    UserEntity user = new UserEntity();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Mock Building and Mapping Entities
    BuildingEntity building = new BuildingEntity();
    building.setId(2);
    building.setAddress("456 Oak Ave");
    building.setCity("Brooklyn");
    building.setState("NY");
    building.setZipCode("46142");
    building.setCreatedDatetime(OffsetDateTime.parse("2024-02-22T14:30Z"));
    building.setModifiedDatetime(OffsetDateTime.parse("2024-10-17T20:10:10.803236Z"));

    BuildingUserMappingEntity buildingUserMapping = new BuildingUserMappingEntity();
    buildingUserMapping.setUser(user);
    buildingUserMapping.setBuilding(building);

    // Mock BuildingFeature and Mapping
    BuildingFeatureEntity feature = new BuildingFeatureEntity();
    feature.setName("Near Hospital");
    BuildingFeatureBuildingMappingEntity featureMapping = new BuildingFeatureBuildingMappingEntity();
    featureMapping.setBuilding(building);
    featureMapping.setBuildingFeature(feature);

    // Setup repository responses
    when(buildingUserMappingRepository.findByUserId(userId)).thenReturn(List.of(buildingUserMapping));
    when(buildingFeatureMappingRepository.findByBuilding(building)).thenReturn(List.of(featureMapping));

    // Mock ObjectNode creation for building JSON
    ObjectNode buildingJson = mock(ObjectNode.class);
    ArrayNode featureArray = mock(ArrayNode.class);
    when(objectMapper.createObjectNode()).thenReturn(buildingJson);
    when(buildingJson.put(anyString(), anyString())).thenReturn(buildingJson);
    when(buildingJson.put(anyString(), anyInt())).thenReturn(buildingJson);
    when(buildingJson.putArray("features")).thenReturn(featureArray);
    when(featureArray.add(anyString())).thenReturn(featureArray);

    // Act
    ResponseEntity<?> response = buildingController.getUserBuildings(userId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    List<ObjectNode> responseBody = (List<ObjectNode>) response.getBody();
    assertEquals(1, responseBody.size());

    // Verify interactions and feature addition
    verify(userRepository, times(1)).findById(userId);
    verify(buildingUserMappingRepository, times(1)).findByUserId(userId);
    verify(buildingFeatureMappingRepository, times(1)).findByBuilding(building);
    verify(buildingJson, times(1)).put("address", "456 Oak Ave");
    verify(buildingJson, times(1)).put("city", "Brooklyn");
    verify(buildingJson, times(1)).put("state", "NY");
    verify(buildingJson, times(1)).put("zip_code", "46142");
    verify(featureArray, times(1)).add("Near Hospital");
  }

  @Test
  void testGetUserBuildings_UserWithNoBuildings() {
    // Arrange
    int userId = 1;
    UserEntity user = new UserEntity();
    user.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(buildingUserMappingRepository.findByUserId(userId)).thenReturn(List.of());

    // Act
    ResponseEntity<?> response = buildingController.getUserBuildings(userId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<?> responseBody = (List<?>) response.getBody();
    assertNotNull(responseBody);
    assertTrue(responseBody.isEmpty());
    verify(userRepository, times(1)).findById(userId);
    verify(buildingUserMappingRepository, times(1)).findByUserId(userId);
    verifyNoInteractions(buildingFeatureMappingRepository);
  }

  @Test
  void testAddExistingBuildingToUser_UserNotFound() {
    // Arrange
    int userId = 1;
    int buildingId = 100;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.addExistingBuildingToUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User with id " + userId + " not found.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(buildingRepository, buildingUserMappingRepository);
  }

  @Test
  void testAddExistingBuildingToUser_BuildingNotFound() {
    // Arrange
    int userId = 1;
    int buildingId = 100;
    UserEntity user = new UserEntity();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.addExistingBuildingToUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Building with id " + buildingId + " not found.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verify(buildingRepository, times(1)).findById(buildingId);
    verifyNoInteractions(buildingUserMappingRepository);
  }

  @Test
  void testAddExistingBuildingToUser_ConflictExistingMapping() {
    // Arrange
    int userId = 1;
    int buildingId = 100;
    UserEntity user = new UserEntity();
    user.setId(userId);
    BuildingEntity building = new BuildingEntity();
    building.setId(buildingId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
    when(buildingUserMappingRepository.findByUserIdAndBuildingId(userId, buildingId))
        .thenReturn(Optional.of(new BuildingUserMappingEntity()));

    // Act
    ResponseEntity<?> response = buildingController.addExistingBuildingToUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("This building is already linked to the user.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verify(buildingRepository, times(1)).findById(buildingId);
    verify(buildingUserMappingRepository, times(1)).findByUserIdAndBuildingId(userId, buildingId);
  }

  @Test
  void testAddExistingBuildingToUser_Success() {
    // Arrange
    int userId = 1;
    int buildingId = 100;
    UserEntity user = new UserEntity();
    user.setId(userId);
    BuildingEntity building = new BuildingEntity();
    building.setId(buildingId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
    when(buildingUserMappingRepository.findByUserIdAndBuildingId(userId, buildingId))
        .thenReturn(Optional.empty());

    // Mock creation of the JSON response
    ObjectNode responseJson = new ObjectMapper().createObjectNode();
    responseJson.put("user_id", userId);
    responseJson.put("building_id", buildingId);
    responseJson.put("status", "Building successfully linked to user.");

    when(objectMapper.createObjectNode()).thenReturn(responseJson);

    // Act
    ResponseEntity<?> response = buildingController.addExistingBuildingToUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(responseJson, response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verify(buildingRepository, times(1)).findById(buildingId);
    verify(buildingUserMappingRepository, times(1)).findByUserIdAndBuildingId(userId, buildingId);
    verify(buildingUserMappingRepository, times(1)).save(any(BuildingUserMappingEntity.class));
  }

 @Test
 void testGetBuildingsSuccessWithAddress(){
     // Arrange
     BuildingEntity building = new BuildingEntity();
     building.setId(1);
     String address = "33 some st";
     building.setAddress(address);
     when(buildingRepository.findByAddress(address)).thenReturn(Optional.of(building));

     // Act
     ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(address, null, null, null);

     // Assert
     assertEquals(HttpStatus.OK, response.getStatusCode());
     assertNotNull(response.getBody());
     assertFalse(response.getBody().isEmpty());
     assertEquals(1, response.getBody().size());
     verify(buildingRepository, times(1)).findByAddress(address);
 }

  @Test
  void testGetBuildingsSuccessWithCity(){
    // Arrange
    BuildingEntity building1 = new BuildingEntity();
    BuildingEntity building2 = new BuildingEntity();
    building1.setId(1);
    building2.setId(2);
    String city = "someCity";
    building1.setCity(city);
    building2.setCity(city);
    when(buildingRepository.findByCity(city)).thenReturn(List.of(building1, building2));

    // Act
    ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(null, city, null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isEmpty());
    assertEquals(2, response.getBody().size());
    verify(buildingRepository, times(1)).findByCity(city);
  }

  @Test
  void testGetBuildingsSuccessWithState(){
    // Arrange
    BuildingEntity building1 = new BuildingEntity();
    BuildingEntity building2 = new BuildingEntity();
    building1.setId(1);
    building2.setId(2);
    String state = "NY";
    building1.setState(state);
    building2.setState(state);
    when(buildingRepository.findByState(state)).thenReturn(List.of(building1, building2));

    // Act
    ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(null, null, state, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isEmpty());
    assertEquals(2, response.getBody().size());
    verify(buildingRepository, times(1)).findByState(state);

  }

  @Test
  void testGetBuildingsAddressNotFound(){

    String address = "999 Lincoln St";
    // Arrange
    when(buildingRepository.findByAddress(address)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.getBuildings(address, null, null, null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(buildingRepository, times(1)).findByAddress(address);
  }

  @Test
  void testGetBuildingsCityNotFound(){
    String city = "Boston";
    // Arrange
    when(buildingRepository.findByCity(city)).thenReturn(List.of()); //empty list

    // Act
    ResponseEntity<?> response = buildingController.getBuildings(null, city, null, null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(buildingRepository, times(1)).findByCity(city);
  }

  @Test
  void testGetBuildingsStateNotFound() {
    String state = "NY";
    //Arrange
    when(buildingRepository.findByState(state)).thenReturn(List.of()); //empty list

    //Act
    ResponseEntity<?> response = buildingController.getBuildings(null, null, state, null);

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(buildingRepository, times(1)).findByState(state);
  }

  @Test
  public void testGetBuildingsZipCodeNotFound() {
    // Arrange
    String zipCode = "00000";
    when(buildingRepository.findByZipCode(zipCode)).thenReturn(List.of());

    // Act
    ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(null, null, null, zipCode);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(buildingRepository, times(1)).findByZipCode(zipCode);
    verify(buildingRepository, never()).findByAddress(anyString());
    verify(buildingRepository, never()).findByCity(anyString());
    verify(buildingRepository, never()).findAll();
  }

  @Test
  public void testGetBuildingsSuccessWithZipCode() {
    // Arrange
    BuildingEntity building1 = new BuildingEntity();
    BuildingEntity building2 = new BuildingEntity();
    building1.setId(1);
    building2.setId(2);
    String zipCode = "00000";
    building1.setZipCode(zipCode);
    building2.setZipCode(zipCode);
    when(buildingRepository.findByZipCode(zipCode)).thenReturn(List.of(building1, building2));

    // Act
    ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(null, null, null, zipCode);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isEmpty());
    assertEquals(2, response.getBody().size());
    verify(buildingRepository, times(1)).findByZipCode(zipCode);
  }

  @Test
  void testGetBuildingsSuccessWithoutAddress() {
    // Arrange
    BuildingEntity building1 = new BuildingEntity();
    building1.setId(1);
    building1.setAddress("123 Test Street");
    building1.setCity("Test City");
    building1.setState("TS");
    building1.setZipCode("12345");

    BuildingEntity building2 = new BuildingEntity();
    building2.setId(2);
    building2.setAddress("456 Another Ave");
    building2.setCity("Another City");
    building2.setState("AC");
    building2.setZipCode("67890");

    List<BuildingEntity> buildings = List.of(building1, building2);

    when(buildingRepository.findAll()).thenReturn(buildings);

    // Act
    ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(null, null, null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(buildingRepository, times(1)).findAll();
  }

  @Test
  void testGetBuildingsNoContent() {
    // Arrange
    when(buildingRepository.findAll()).thenReturn(List.of());

    // Act
    ResponseEntity<List<BuildingEntity>> response = buildingController.getBuildings(null, null, null, null);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(buildingRepository, times(1)).findAll();
  }

  @Test
  void testGetHousingUnitsByBuildingSuccess() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);

    HousingUnitEntity unit1 = new HousingUnitEntity();
    unit1.setId(1);
    unit1.setUnitNumber("101");
    unit1.setBuilding(building);

    HousingUnitEntity unit2 = new HousingUnitEntity();
    unit2.setId(2);
    unit2.setUnitNumber("102");
    unit2.setBuilding(building);

    building.setHousingUnits(Set.of(unit1, unit2));

    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Act
    ResponseEntity<Set<HousingUnitEntity>> response = buildingController.getHousingUnitsByBuilding(1);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(buildingRepository, times(1)).findById(1);
  }

  @Test
  void testGetHousingUnitsByBuildingNotFound() {
      // Arrange
      when(buildingRepository.findById(999)).thenReturn(Optional.empty());
  
      // Act
      ResponseEntity<Set<HousingUnitEntity>> response = buildingController.getHousingUnitsByBuilding(999);
  
      // Assert
      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertNull(response.getBody());
      verify(buildingRepository, times(1)).findById(999);
  }
  
  @Test
  void testGetHousingUnitsByBuildingNoContent() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setHousingUnits(Set.of());

    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Act
    ResponseEntity<Set<HousingUnitEntity>> response = buildingController.getHousingUnitsByBuilding(1);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(buildingRepository, times(1)).findById(1);
  }

  // @Test
  // public void testRemoveBuildingFromUser_Success() {
  //   // Arrange
  //   int userId = 1;
  //   int buildingId = 1;

  //   UserEntity user = new UserEntity();
  //   user.setId(userId);

  //   BuildingEntity building = new BuildingEntity();
  //   building.setId(buildingId);

  //   BuildingUserMappingEntity mapping = new BuildingUserMappingEntity();
  //   mapping.setUser(user);
  //   mapping.setBuilding(building);

  //   // Mock repository responses
  //   when(userRepository.findById(userId)).thenReturn(Optional.of(user));
  //   when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
  //   when(buildingUserMappingRepository.findByUserIdAndBuildingId(userId, buildingId))
  //       .thenReturn(Optional.of(mapping));

  //   ObjectNode responseJson = new ObjectMapper().createObjectNode();
  //   responseJson.put("user_id", userId);
  //   responseJson.put("building_id", buildingId);
  //   responseJson.put("status", "Building successfully unlinked from user.");

  //   // Act
  //   ResponseEntity<?> response = buildingController.removeBuildingFromUser(userId, buildingId);

  //   // Assert
  //   assertEquals(HttpStatus.OK, response.getStatusCode());
  //   assertTrue(response.getBody().toString().contains("Building successfully unlinked from user."));
  //   verify(buildingUserMappingRepository, times(1)).delete(mapping);
  // }

  @Test
  public void testRemoveBuildingFromUser_UserNotFound() {
    // Arrange
    int userId = 1;
    int buildingId = 1;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.removeBuildingFromUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("User with id " + userId + " not found."));
    verifyNoInteractions(buildingRepository);
    verifyNoInteractions(buildingUserMappingRepository);
  }

  @Test
  public void testRemoveBuildingFromUser_BuildingNotFound() {
    // Arrange
    int userId = 1;
    int buildingId = 1;

    UserEntity user = new UserEntity();
    user.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.removeBuildingFromUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building with id " + buildingId + " not found."));
    verifyNoInteractions(buildingUserMappingRepository);
  }

  @Test
  public void testRemoveBuildingFromUser_MappingNotFound() {
    // Arrange
    int userId = 1;
    int buildingId = 1;

    UserEntity user = new UserEntity();
    user.setId(userId);

    BuildingEntity building = new BuildingEntity();
    building.setId(buildingId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
    when(buildingUserMappingRepository.findByUserIdAndBuildingId(userId, buildingId))
        .thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = buildingController.removeBuildingFromUser(userId, buildingId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("This building is not linked to the user."));
    verify(buildingUserMappingRepository, times(0)).delete(any());
  }
}
