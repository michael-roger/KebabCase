package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.coms4156.project.kebabcase.controller.BuildingFeatureController;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BuildingFeatureControllerUnitTests {

  @Mock
  private BuildingFeatureRepositoryInterface buildingFeatureRepository;

  @InjectMocks
  private BuildingFeatureController buildingFeatureController;

  @BeforeEach
  void setUp() {
    // Initialize mocks and inject them into the controller
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetBuildingFeaturesSuccess() {
    BuildingFeatureEntity feature = new BuildingFeatureEntity();
    feature.setId(1);
    feature.setName("Elevator");

    BuildingFeatureEntity feature2 = new BuildingFeatureEntity();
    feature2.setId(2);
    feature2.setName("Near Accessible Subway");

    List<BuildingFeatureEntity> features = new ArrayList<>();
    features.add(feature);
    features.add(feature2);

    when(buildingFeatureRepository.findAll()).thenReturn(features);

    ResponseEntity<List<BuildingFeatureEntity>> response =
        buildingFeatureController.getBuildingFeatures();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(buildingFeatureRepository, times(1)).findAll();
  }

  @Test
  void testGetBuildingFeaturesFailure() {

    when(buildingFeatureRepository.findAll()).thenReturn(List.of());

    ResponseEntity<List<BuildingFeatureEntity>> response =
            buildingFeatureController.getBuildingFeatures();

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(buildingFeatureRepository, times(1)).findAll();
  }

}
