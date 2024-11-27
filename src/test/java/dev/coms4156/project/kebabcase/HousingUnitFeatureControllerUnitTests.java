package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.coms4156.project.kebabcase.controller.HousingUnitFeatureController;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.repository.HousingUnitFeatureRepositoryInterface;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HousingUnitFeatureControllerUnitTests {

  @Mock
  private HousingUnitFeatureRepositoryInterface housingUnitFeatureRepository;

  @InjectMocks
  private HousingUnitFeatureController housingUnitFeatureController;

  @BeforeEach
  void setUp() {
    // Initialize mocks and inject them into the controller
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetHousingUnitFeaturesSuccess() {
    HousingUnitFeatureEntity feature = new HousingUnitFeatureEntity();
    feature.setId(1);
    feature.setName("Elevator");

    HousingUnitFeatureEntity feature2 = new HousingUnitFeatureEntity();
    feature2.setId(2);
    feature2.setName("Near Accessible Subway");

    List<HousingUnitFeatureEntity> features = new ArrayList<>();
    features.add(feature);
    features.add(feature2);

    when(housingUnitFeatureRepository.findAll()).thenReturn(features);

    ResponseEntity<List<HousingUnitFeatureEntity>> response =
        housingUnitFeatureController.getBuildingFeatures();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(housingUnitFeatureRepository, times(1)).findAll();
  }

  @Test
  void testGetBuildingFeaturesFailure() {

    when(housingUnitFeatureRepository.findAll()).thenReturn(List.of());

    ResponseEntity<List<HousingUnitFeatureEntity>> response =
            housingUnitFeatureController.getBuildingFeatures();

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(housingUnitFeatureRepository, times(1)).findAll();
  }

}
