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
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureHousingUnitMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;

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
  private ObjectMapper objectMapper;

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

    // Act
    ResponseEntity<?> response = housingUnitController.getHousingUnit(1);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ObjectNode jsonResponse = (ObjectNode) response.getBody();
    assertNotNull(jsonResponse);
    assertEquals("101", jsonResponse.get("unit_number").asText());

    ObjectNode buildingJson = (ObjectNode) jsonResponse.get("building");
    assertEquals("123 Main St", buildingJson.get("address").asText());
    assertEquals("Test City", buildingJson.get("city").asText());

    ArrayNode buildingFeatures = (ArrayNode) buildingJson.get("features");
    assertEquals("Gym", buildingFeatures.get(0).asText());

    ArrayNode housingUnitFeatures = (ArrayNode) jsonResponse.get("housing_unit_features");
    assertEquals("Balcony", housingUnitFeatures.get(0).asText());

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

    // Act
    ResponseEntity<?> response = housingUnitController.getHousingUnit(1);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ObjectNode jsonResponse = (ObjectNode) response.getBody();
    assertNotNull(jsonResponse);
    assertEquals("101", jsonResponse.get("unit_number").asText());

    ObjectNode buildingJson = (ObjectNode) jsonResponse.get("building");
    assertEquals("123 Main St", buildingJson.get("address").asText());

    ArrayNode buildingFeatures = (ArrayNode) buildingJson.get("features");
    assertEquals(0, buildingFeatures.size()); // No building features

    ArrayNode housingUnitFeatures = (ArrayNode) jsonResponse.get("housing_unit_features");
    assertEquals(0, housingUnitFeatures.size()); // No housing unit features

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
}
