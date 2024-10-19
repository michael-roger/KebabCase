package dev.coms4156.project.kebabcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.controller.HousingUnitController;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
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
import static org.mockito.Mockito.*;

class HousingUnitControllerUnitTests {

  @Mock
  private HousingUnitRepositoryInterface housingUnitRepository;

  @Mock
  private BuildingRepositoryInterface buildingRepository;

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
  void testGetHousingUnitByIdSuccess() {
    // Arrange
    HousingUnitEntity unit = new HousingUnitEntity();
    unit.setId(1);
    unit.setUnitNumber("Unit 101");
    unit.setCreatedDatetime(OffsetDateTime.now());
    unit.setModifiedDatetime(OffsetDateTime.now());

    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    unit.setBuilding(building);

    when(housingUnitRepository.findById(1)).thenReturn(Optional.of(unit));

    ObjectNode jsonNode = mock(ObjectNode.class);
    when(objectMapper.createObjectNode()).thenReturn(jsonNode);
    when(jsonNode.put(anyString(), anyString())).thenReturn(jsonNode);

    // Act
    ResponseEntity<?> result = housingUnitController.getHousingUnit(1);

    // Assert
    assertEquals(HttpStatus.OK, result.getStatusCode());
    verify(housingUnitRepository, times(1)).findById(1);
  }

  @Test
  void testGetHousingUnitByIdNotFound() {
    // Arrange
    when(housingUnitRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<?> result = housingUnitController.getHousingUnit(999);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    verify(housingUnitRepository, times(1)).findById(999);
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
}
