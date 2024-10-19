package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.controller.BuildingController;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class BuildingControllerUnitTests {

  @Mock
  private BuildingRepositoryInterface buildingRepository;

  @Mock
  private BuildingFeatureRepositoryInterface buildingFeatureRepository;

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
    ObjectNode result = buildingController.getBuildingById(1);

    // Assert
    assertNotNull(result); // Check that the result is not null
    // Verify that repository is called once
    verify(buildingRepository, times(1)).findById(1);
  }

  @Test
  void testGetBuildingByIdNotFound() {
    // Arrange
    when(buildingRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> buildingController.getBuildingById(999));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(buildingRepository, times(1)).findById(999);
  }

  @Test
  void testCreateBuildingSuccess() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setId(1);
    building.setAddress("123 Test Street");
    building.setCity("Test City");
    building.setState("TS");
    building.setZipCode("12345");

    when(buildingRepository.findByAddressAndCityAndStateAndZipCode(
        anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty()); // No duplicate address
    // Save the building
    when(buildingRepository.save(any(BuildingEntity.class))).thenReturn(building);

    // Act
    ResponseEntity<?> response = buildingController.createBuilding(
        "123 Test Street", "Test City",
        "TS", "12345", null);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Building was added succesfully"));
    verify(buildingRepository, times(1))
        .save(any(BuildingEntity.class));
  }

  @Test
  void testCreateBuildingConflict() {
    // Arrange
    BuildingEntity existingBuilding = new BuildingEntity();
    existingBuilding.setId(1);
    existingBuilding.setAddress("123 Test Street");

    when(buildingRepository.findByAddressAndCityAndStateAndZipCode(
        anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(existingBuilding)); // Duplicate address found

    // Act
    ResponseEntity<?> response = buildingController.createBuilding(
        "123 Test Street", "Test City",
        "TS", "12345", null);

    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    verify(buildingRepository, times(0))
        .save(any(BuildingEntity.class));
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
}
