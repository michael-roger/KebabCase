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
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;

import java.time.OffsetDateTime;
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
}
