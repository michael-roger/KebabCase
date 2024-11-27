package dev.coms4156.project.kebabcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.coms4156.project.kebabcase.controller.HousingUnitController;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitUserMappingEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureHousingUnitMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;

class HousingUnitControllerUnitTests {

  @Mock
  private HousingUnitRepositoryInterface housingUnitRepository;

  @Mock
  private BuildingRepositoryInterface buildingRepository;

  @Mock
  private BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository;

  @Mock
  private HousingUnitFeatureRepositoryInterface unitFeatureRepository;

  @Mock
  private HousingUnitFeatureHousingUnitMappingRepositoryInterface unitFeatureMappingRepository;

  @Mock
  private HousingUnitUserMappingRepositoryInterface unitUserMappingRepository;

  @Mock
  private UserRepositoryInterface userRepository;

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private HousingUnitController housingUnitController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);  // Initialize mocks
  }

  @Test
  void testGetBuildingHousingUnitsSuccess() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);

    HousingUnitEntity unit1 = new HousingUnitEntity();
    unit1.setId(1);
    unit1.setUnitNumber("Unit 101");

    HousingUnitEntity unit2 = new HousingUnitEntity();
    unit2.setId(2);
    unit2.setUnitNumber("Unit 102");

    List<HousingUnitEntity> units = new ArrayList<>();
    units.add(unit1);
    units.add(unit2);

    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));
    when(housingUnitRepository.findByBuilding(building)).thenReturn(units);

    ObjectNode node1 = mock(ObjectNode.class);
    ObjectNode node2 = mock(ObjectNode.class);

    when(objectMapper.createObjectNode()).thenReturn(node1, node2);
    when(node1.put(anyString(), (String) any())).thenReturn(node1);
    when(node2.put(anyString(), (String) any())).thenReturn(node2);

    // Act
    List<ObjectNode> result = housingUnitController.getBuildingHousingUnits(1);

    // Assert
    assertEquals(2, result.size());
    verify(housingUnitRepository, times(1)).findByBuilding(building);
  }

  @Test
  void testGetBuildingHousingUnitsNotFound() {
    // Arrange
    when(buildingRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> housingUnitController.getBuildingHousingUnits(999));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(buildingRepository, times(1)).findById(999);
  }

  @Test
  void testGetHousingUnitSuccess() {
    // Arrange
    HousingUnitEntity housingUnit = new HousingUnitEntity();
    housingUnit.setId(1);
    housingUnit.setUnitNumber("101");
    housingUnit.setCreatedDatetime(OffsetDateTime.now());
    housingUnit.setModifiedDatetime(OffsetDateTime.now());

    BuildingEntity building = new BuildingEntity();
    building.setId(10);
    building.setAddress("123 Main St");
    building.setCity("Test City");
    building.setState("TS");
    building.setZipCode("12345");
    housingUnit.setBuilding(building);

    // Mock repository and entity relationships
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(housingUnit));

    BuildingFeatureEntity buildingFeature = new BuildingFeatureEntity();
    buildingFeature.setName("Gym");
    BuildingFeatureBuildingMappingEntity buildingFeatureMapping = new BuildingFeatureBuildingMappingEntity();
    buildingFeatureMapping.setBuilding(building);
    buildingFeatureMapping.setBuildingFeature(buildingFeature);
    when(buildingFeatureMappingRepository.findByBuilding(building)).thenReturn(List.of(buildingFeatureMapping));

    HousingUnitFeatureEntity housingUnitFeature = new HousingUnitFeatureEntity();
    housingUnitFeature.setName("Balcony");
    HousingUnitFeatureHousingUnitMappingEntity housingUnitFeatureMapping = new HousingUnitFeatureHousingUnitMappingEntity();
    housingUnitFeatureMapping.setHousingUnit(housingUnit);
    housingUnitFeatureMapping.setHousingUnitFeature(housingUnitFeature);
    when(unitFeatureMappingRepository.findByHousingUnit(housingUnit)).thenReturn(List.of(housingUnitFeatureMapping));

    ObjectNode json = mock(ObjectNode.class);
    ObjectNode buildingJson = mock(ObjectNode.class);
    ArrayNode buildingFeaturesJson = mock(ArrayNode.class);
    ArrayNode housingUnitFeaturesJson = mock(ArrayNode.class);

    when(objectMapper.createObjectNode()).thenReturn(json, buildingJson);

    // Mock the json structure for housing unit
    when(json.put(eq("id"), eq(1))).thenReturn(json);
    when(json.put(eq("unit_number"), eq("101"))).thenReturn(json);
    when(json.put(eq("created_datetime"), anyString())).thenReturn(json);
    when(json.put(eq("modified_datetime"), anyString())).thenReturn(json);

    // Mock the json structure for building details
    when(json.putObject("building")).thenReturn(buildingJson);
    when(buildingJson.put(eq("id"), eq(10))).thenReturn(buildingJson);
    when(buildingJson.put(eq("address"), eq("123 Main St"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("city"), eq("Test City"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("state"), eq("TS"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("zip_code"), eq("12345"))).thenReturn(buildingJson);

    // Mock arrays for features
    when(buildingJson.putArray("features")).thenReturn(buildingFeaturesJson);
    when(json.putArray("housing_unit_features")).thenReturn(housingUnitFeaturesJson);
    when(buildingFeaturesJson.add("Gym")).thenReturn(buildingFeaturesJson);
    when(housingUnitFeaturesJson.add("Balcony")).thenReturn(housingUnitFeaturesJson);

    // Act
    ResponseEntity<?> response = housingUnitController.getHousingUnit(1);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(housingUnitRepository, times(1)).findById(1);
    verify(buildingFeatureMappingRepository, times(1)).findByBuilding(building);
    verify(unitFeatureMappingRepository, times(1)).findByHousingUnit(housingUnit);
  }

  @Test
  void testGetHousingUnitNotFound() {
    // Arrange
    when(housingUnitRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = housingUnitController.getHousingUnit(999);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Housing unit with id 999 not found", response.getBody());
    verify(housingUnitRepository, times(1)).findById(999);
  }

  @Test
  void testGetHousingUnitWithNoFeatures() {
    // Arrange
    HousingUnitEntity housingUnit = new HousingUnitEntity();
    housingUnit.setId(1);
    housingUnit.setUnitNumber("101");
    housingUnit.setCreatedDatetime(OffsetDateTime.now());
    housingUnit.setModifiedDatetime(OffsetDateTime.now());

    BuildingEntity building = new BuildingEntity();
    building.setId(10);
    building.setAddress("123 Main St");
    building.setCity("Test City");
    building.setState("TS");
    building.setZipCode("12345");
    housingUnit.setBuilding(building);

    // Mock repository and empty features
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(housingUnit));
    when(buildingFeatureMappingRepository.findByBuilding(building)).thenReturn(List.of());
    when(unitFeatureMappingRepository.findByHousingUnit(housingUnit)).thenReturn(List.of());

    ObjectNode json = mock(ObjectNode.class);
    ObjectNode buildingJson = mock(ObjectNode.class);
    ArrayNode buildingFeaturesJson = mock(ArrayNode.class);
    ArrayNode housingUnitFeaturesJson = mock(ArrayNode.class);

    // Mock the ObjectMapper to return these nodes
    when(objectMapper.createObjectNode()).thenReturn(json, buildingJson);
    
    // Mock the put methods on ObjectNode with matchers
    when(json.put(eq("id"), eq(1))).thenReturn(json);
    when(json.put(eq("unit_number"), eq("101"))).thenReturn(json);
    when(json.put(eq("created_datetime"), anyString())).thenReturn(json);
    when(json.put(eq("modified_datetime"), anyString())).thenReturn(json);
    
    when(json.putObject(eq("building"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("id"), eq(10))).thenReturn(buildingJson);
    when(buildingJson.put(eq("address"), eq("123 Main St"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("city"), eq("Test City"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("state"), eq("TS"))).thenReturn(buildingJson);
    when(buildingJson.put(eq("zip_code"), eq("12345"))).thenReturn(buildingJson);
    
    when(buildingJson.putArray(eq("features"))).thenReturn(buildingFeaturesJson);
    when(json.putArray(eq("housing_unit_features"))).thenReturn(housingUnitFeaturesJson);

    // Act
    ResponseEntity<?> response = housingUnitController.getHousingUnit(1);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(housingUnitRepository, times(1)).findById(1);
    verify(buildingFeatureMappingRepository, times(1)).findByBuilding(building);
    verify(unitFeatureMappingRepository, times(1)).findByHousingUnit(housingUnit);
  }

  @Test
  void testCreateHousingUnitSuccess() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);

    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    unit.setUnitNumber("Unit 101");
    unit.setCreatedDatetime(OffsetDateTime.now());
    unit.setModifiedDatetime(OffsetDateTime.now());

    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));
    when(housingUnitRepository.findByBuildingAndUnitNumber(any(BuildingEntity.class), anyString()))
        .thenReturn(Optional.empty());
    when(housingUnitRepository.save(any(HousingUnitEntity.class))).thenReturn(unit);

    // Act
    ResponseEntity<?> result = housingUnitController.createBuilding(1, "Unit 101", null);

    // Assert
    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertTrue(result.getBody().toString().contains("Housing Unit was added succesfully"));
    verify(housingUnitRepository, times(1)).save(any(HousingUnitEntity.class));
  }

  @Test
  void testCreateHousingUnitBuildingNotFound() {
    // Arrange
    when(buildingRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> result = housingUnitController.createBuilding(999, "Unit 101", null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    assertTrue(result.getBody().toString().contains("Building with id 999 not found"));
  }

  @Test
  void testCreateHousingUnitConflict() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);

    HousingUnitEntity existingUnit = new HousingUnitEntity();
    existingUnit.setId(1);
    existingUnit.setUnitNumber("Unit 101");

    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));
    when(housingUnitRepository.findByBuildingAndUnitNumber(any(BuildingEntity.class), anyString()))
        .thenReturn(Optional.of(existingUnit));

    // Act
    ResponseEntity<?> result = housingUnitController.createBuilding(1, "Unit 101", null);

    // Assert
    assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    assertTrue(result.getBody().toString().contains("A housing unit in the same building already exists."));
  }

  @Test
  void testCreateHousingUnitPartialContent() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);

    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    unit.setUnitNumber("Unit 101");
    unit.setCreatedDatetime(OffsetDateTime.now());
    unit.setModifiedDatetime(OffsetDateTime.now());

    List<Integer> features = List.of(1, 2, -1);  // Adding both valid and invalid feature IDs

    // Mock building existence
    when(buildingRepository.findById(1)).thenReturn(Optional.of(building));

    // Mock housing unit non-existence
    when(housingUnitRepository.findByBuildingAndUnitNumber(any(BuildingEntity.class), anyString()))
        .thenReturn(Optional.empty());

    // Mock saving the new housing unit
    when(housingUnitRepository.save(any(HousingUnitEntity.class))).thenReturn(unit);

    // Mock feature lookup: feature IDs 1 and 2 exist, but -1 does not
    when(unitFeatureRepository.findById(1)).thenReturn(Optional.of(new HousingUnitFeatureEntity()));
    when(unitFeatureRepository.findById(2)).thenReturn(Optional.of(new HousingUnitFeatureEntity()));
    when(unitFeatureRepository.findById(-1)).thenReturn(Optional.empty());  // Feature -1 is missing

    // Act
    ResponseEntity<?> result = housingUnitController.createBuilding(1, "Unit 101", features);

    // Assert
    assertEquals(HttpStatus.PARTIAL_CONTENT, result.getStatusCode());
    assertTrue(result.getBody().toString().contains("Building created, but the following feature IDs were not found: [-1]"));
    verify(housingUnitRepository, times(1)).save(any(HousingUnitEntity.class));
    verify(unitFeatureRepository, times(3)).findById(anyInt());  // Ensures each feature ID is checked
  }

  @Test
  void testUpdateHousingUnitNotFound() {
    // Arrange
    when(housingUnitRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> result = housingUnitController.updateBuilding(999, null, null, null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    assertEquals("Building not found", result.getBody());
    verify(housingUnitRepository, times(1)).findById(999);
  }

  @Test
  void testUpdateHousingUnitNoFieldsProvided() {
    // Arrange
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(unit));

    // Act
    ResponseEntity<?> result = housingUnitController.updateBuilding(1, null, null, null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    assertEquals("No fields provided for update", result.getBody());
  }

  @Test
  void testUpdateHousingUnitConflictInAddAndRemoveFeatures() {
    // Arrange
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(unit));
    List<Integer> addFeatures = List.of(1, 2);
    List<Integer> removeFeatures = List.of(2, 3); // Conflict on feature ID 2

    // Act
    ResponseEntity<?> result = housingUnitController.updateBuilding(1, null, addFeatures, removeFeatures);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    assertEquals("Conflict: Feature IDs present in both add and remove lists: [2]", result.getBody());
  }

  @Test
  void testUpdateHousingUnitPartialContentInvalidFeatures() {
    // Arrange
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    unit.setUnitNumber("Unit 101");
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(unit));

    List<Integer> addFeatures = List.of(1, -1);  // Assume feature ID -1 is invalid

    when(unitFeatureRepository.findById(1)).thenReturn(Optional.of(new HousingUnitFeatureEntity()));
    when(unitFeatureRepository.findById(-1)).thenReturn(Optional.empty()); // Feature -1 not found

    // Act
    ResponseEntity<?> result = housingUnitController.updateBuilding(1, "New Unit 102", addFeatures, null);

    // Assert
    assertEquals(HttpStatus.PARTIAL_CONTENT, result.getStatusCode());
    assertEquals("Housing unit updated, but the following feature IDs were not found: [-1]", result.getBody());
  }

  @Test
  void testUpdateHousingUnitAllInvalidFeatures() {
    // Arrange
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    // unit.setUnitNumber("Unit 101");
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(unit));

    List<Integer> addFeatures = List.of(-1, -2); // Assume all IDs are invalid

    when(unitFeatureRepository.findById(-1)).thenReturn(Optional.empty());
    when(unitFeatureRepository.findById(-2)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> result = housingUnitController.updateBuilding(1, null, addFeatures, null);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    assertEquals("Could not find any of the housing unit features requested.", result.getBody());
  }

  @Test
  void testUpdateHousingUnitSuccess() {
    // Arrange
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    unit.setUnitNumber("Unit 101");
    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(unit));

    List<Integer> addFeatures = List.of(1, 2);
    HousingUnitFeatureEntity feature1 = new HousingUnitFeatureEntity();
    HousingUnitFeatureEntity feature2 = new HousingUnitFeatureEntity();

    when(unitFeatureRepository.findById(1)).thenReturn(Optional.of(feature1));
    when(unitFeatureRepository.findById(2)).thenReturn(Optional.of(feature2));
    when(unitFeatureMappingRepository.findByHousingUnitAndHousingUnitFeature(unit, feature1)).thenReturn(Optional.empty());
    when(unitFeatureMappingRepository.findByHousingUnitAndHousingUnitFeature(unit, feature2)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> result = housingUnitController.updateBuilding(1, "New Unit 102", addFeatures, null);

    // Assert
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("Housing unit info has been successfully updated!", result.getBody());
  }

  @Test
  void testGetUserHousingUnits_UserNotFound() {
    // Arrange
    int userId = 999;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = housingUnitController.getUserHousingUnits(userId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User with id " + userId + " not found.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(unitUserMappingRepository);
  }

  @Test
  void testGetUserHousingUnits_SuccessWithExistingUser() {
    when(objectMapper.createObjectNode()).thenAnswer(invocation -> new ObjectMapper().createObjectNode());

    // Arrange
    int userId = 1;

    // Mock UserEntity
    UserEntity user = new UserEntity();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Mock HousingUnit and Building Entities
    HousingUnitEntity housingUnit1 = new HousingUnitEntity();
    housingUnit1.setId(3);
    housingUnit1.setUnitNumber("2A");
    housingUnit1.setCreatedDatetime(OffsetDateTime.parse("2024-02-23T10:00:00Z"));
    housingUnit1.setModifiedDatetime(OffsetDateTime.parse("2024-02-23T10:00:00Z"));

    HousingUnitEntity housingUnit2 = new HousingUnitEntity();
    housingUnit2.setId(6);
    housingUnit2.setUnitNumber("4A");
    housingUnit2.setCreatedDatetime(OffsetDateTime.parse("2024-07-08T10:00:00Z"));
    housingUnit2.setModifiedDatetime(OffsetDateTime.parse("2024-03-16T10:00:00Z"));

    BuildingEntity building1 = new BuildingEntity();
    building1.setId(2);
    building1.setAddress("456 Oak Ave");
    building1.setCity("Brooklyn");
    building1.setState("NY");
    building1.setZipCode("46142");

    BuildingEntity building2 = new BuildingEntity();
    building2.setId(4);
    building2.setAddress("111 Jojo St");
    building2.setCity("Bronx");
    building2.setState("NY");
    building2.setZipCode("99999");

    housingUnit1.setBuilding(building1);
    housingUnit2.setBuilding(building2);

    // Mock Mappings and Features
    HousingUnitUserMappingEntity mapping1 = new HousingUnitUserMappingEntity();
    mapping1.setUser(user);
    mapping1.setHousingUnit(housingUnit1);

    HousingUnitUserMappingEntity mapping2 = new HousingUnitUserMappingEntity();
    mapping2.setUser(user);
    mapping2.setHousingUnit(housingUnit2);

    HousingUnitFeatureEntity feature1 = new HousingUnitFeatureEntity();
    feature1.setName("Ground Floor");

    HousingUnitFeatureEntity feature2 = new HousingUnitFeatureEntity();
    feature2.setName("Wheelchair Accessible");

    HousingUnitFeatureHousingUnitMappingEntity featureMapping1 = new HousingUnitFeatureHousingUnitMappingEntity();
    featureMapping1.setHousingUnit(housingUnit1);
    featureMapping1.setHousingUnitFeature(feature1);

    HousingUnitFeatureHousingUnitMappingEntity featureMapping2 = new HousingUnitFeatureHousingUnitMappingEntity();
    featureMapping2.setHousingUnit(housingUnit2);
    featureMapping2.setHousingUnitFeature(feature2);

    BuildingFeatureEntity buildingFeature1 = new BuildingFeatureEntity();
    buildingFeature1.setName("Near Hospital");

    BuildingFeatureEntity buildingFeature2 = new BuildingFeatureEntity();
    buildingFeature2.setName("Ramps");

    BuildingFeatureEntity buildingFeature3 = new BuildingFeatureEntity();
    buildingFeature3.setName("Elevator");

    BuildingFeatureBuildingMappingEntity buildingFeatureMapping1 = new BuildingFeatureBuildingMappingEntity();
    buildingFeatureMapping1.setBuilding(building1);
    buildingFeatureMapping1.setBuildingFeature(buildingFeature1);

    BuildingFeatureBuildingMappingEntity buildingFeatureMapping2 = new BuildingFeatureBuildingMappingEntity();
    buildingFeatureMapping2.setBuilding(building2);
    buildingFeatureMapping2.setBuildingFeature(buildingFeature2);

    BuildingFeatureBuildingMappingEntity buildingFeatureMapping3 = new BuildingFeatureBuildingMappingEntity();
    buildingFeatureMapping3.setBuilding(building2);
    buildingFeatureMapping3.setBuildingFeature(buildingFeature3);

    // Setup repository responses
    when(unitUserMappingRepository.findByUserId(userId)).thenReturn(List.of(mapping1, mapping2));
    when(unitFeatureMappingRepository.findByHousingUnit(housingUnit1)).thenReturn(List.of(featureMapping1));
    when(unitFeatureMappingRepository.findByHousingUnit(housingUnit2)).thenReturn(List.of(featureMapping2));
    when(buildingFeatureMappingRepository.findByBuilding(building1)).thenReturn(List.of(buildingFeatureMapping1));
    when(buildingFeatureMappingRepository.findByBuilding(building2)).thenReturn(List.of(buildingFeatureMapping2, buildingFeatureMapping3));

    // Act
    ResponseEntity<?> response = housingUnitController.getUserHousingUnits(userId);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Map the response body by housing unit id for easier access
    List<ObjectNode> responseBody = (List<ObjectNode>) response.getBody();
    assertNotNull(responseBody);
    assertEquals(2, responseBody.size());

    // Create a map of response JSON objects by their "id"
    Map<Integer, ObjectNode> housingUnitsById = responseBody.stream()
        .collect(Collectors.toMap(unit -> unit.get("id").asInt(), unit -> unit));

    // Assert details of unit with id 3
    ObjectNode unit1Json = housingUnitsById.get(3);
    assertNotNull(unit1Json);
    assertEquals("2A", unit1Json.get("unit_number").asText());
    assertEquals("2024-02-23T10:00:00Z", unit1Json.get("created_datetime").asText());
    assertEquals("2024-02-23T10:00:00Z", unit1Json.get("modified_datetime").asText());
    assertEquals("456 Oak Ave", unit1Json.get("building").get("address").asText());
    assertEquals("Brooklyn", unit1Json.get("building").get("city").asText());
    assertEquals("NY", unit1Json.get("building").get("state").asText());
    assertEquals("46142", unit1Json.get("building").get("zip_code").asText());
    assertEquals("Near Hospital", unit1Json.get("building").get("features").get(0).asText());
    assertEquals("Ground Floor", unit1Json.get("housing_unit_features").get(0).asText());

    // Assert details of unit with id 6
    ObjectNode unit2Json = housingUnitsById.get(6);
    assertNotNull(unit2Json);
    assertEquals("4A", unit2Json.get("unit_number").asText());
    assertEquals("2024-07-08T10:00:00Z", unit2Json.get("created_datetime").asText());
    assertEquals("2024-03-16T10:00:00Z", unit2Json.get("modified_datetime").asText());
    assertEquals("111 Jojo St", unit2Json.get("building").get("address").asText());
    assertEquals("Bronx", unit2Json.get("building").get("city").asText());
    assertEquals("NY", unit2Json.get("building").get("state").asText());
    assertEquals("99999", unit2Json.get("building").get("zip_code").asText());
    assertEquals("Ramps", unit2Json.get("building").get("features").get(0).asText());
    assertEquals("Elevator", unit2Json.get("building").get("features").get(1).asText());
    assertEquals("Wheelchair Accessible", unit2Json.get("housing_unit_features").get(0).asText());
  }

  @Test
  void testGetUserHousingUnits_UserWithNoHousingUnits() {
    // Arrange
    int userId = 1;
    UserEntity user = new UserEntity();
    user.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(unitUserMappingRepository.findByUserId(userId)).thenReturn(List.of());

    // Act
    ResponseEntity<?> response = housingUnitController.getUserHousingUnits(userId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<?> responseBody = (List<?>) response.getBody();
    assertNotNull(responseBody);
    assertTrue(responseBody.isEmpty());
    verify(userRepository, times(1)).findById(userId);
    verify(unitUserMappingRepository, times(1)).findByUserId(userId);
  }

  @Test
  void testAddExistingUnitToUser_UserNotFound() {
    // Arrange
    int userId = 1;
    int housingUnitId = 100;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = housingUnitController.addExistingUnitToUser(userId, housingUnitId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User with id " + userId + " not found.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(housingUnitRepository, unitUserMappingRepository);
  }

  @Test
  void testAddExistingUnitToUser_UnitNotFound() {
    // Arrange
    int userId = 1;
    int housingUnitId = 100;
    UserEntity user = new UserEntity();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(housingUnitRepository.findById(housingUnitId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> response = housingUnitController.addExistingUnitToUser(userId, housingUnitId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Housing unit with id " + housingUnitId + " not found.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verify(housingUnitRepository, times(1)).findById(housingUnitId);
    verifyNoInteractions(unitUserMappingRepository);
  }

  @Test
  void testAddExistingUnitToUser_ConflictExistingMapping() {
    // Arrange
    int userId = 1;
    int housingUnitId = 100;
    UserEntity user = new UserEntity();
    user.setId(userId);
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(housingUnitId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(housingUnitRepository.findById(housingUnitId)).thenReturn(Optional.of(unit));
    when(unitUserMappingRepository.findByUserIdAndHousingUnitId(userId, housingUnitId))
        .thenReturn(Optional.of(new HousingUnitUserMappingEntity()));

    // Act
    ResponseEntity<?> response = housingUnitController.addExistingUnitToUser(userId, housingUnitId);

    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("This housing unit is already linked to the user.", response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verify(housingUnitRepository, times(1)).findById(housingUnitId);
    verify(unitUserMappingRepository, times(1)).findByUserIdAndHousingUnitId(userId, housingUnitId);
  }

  @Test
  void testAddExistingUnitToUser_Success() {
    // Arrange
    int userId = 1;
    int housingUnitId = 100;
    UserEntity user = new UserEntity();
    user.setId(userId);
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(housingUnitId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(housingUnitRepository.findById(housingUnitId)).thenReturn(Optional.of(unit));
    when(unitUserMappingRepository.findByUserIdAndHousingUnitId(userId, housingUnitId))
        .thenReturn(Optional.empty());

    // Mock creation of the JSON response
    ObjectNode responseJson = new ObjectMapper().createObjectNode();
    responseJson.put("user_id", userId);
    responseJson.put("housing_unit_id", housingUnitId);
    responseJson.put("status", "Housing unit successfully linked to user.");

    when(objectMapper.createObjectNode()).thenReturn(responseJson);

    // Act
    ResponseEntity<?> response = housingUnitController.addExistingUnitToUser(userId, housingUnitId);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(responseJson, response.getBody());
    verify(userRepository, times(1)).findById(userId);
    verify(housingUnitRepository, times(1)).findById(housingUnitId);
    verify(unitUserMappingRepository, times(1)).findByUserIdAndHousingUnitId(userId, housingUnitId);
    verify(unitUserMappingRepository, times(1)).save(any(HousingUnitUserMappingEntity.class));
  }

}
