package dev.coms4156.project.kebabcase;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import dev.coms4156.project.kebabcase.entity.BuildingUserMappingEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitUserMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
class BuildingControllerIntegrationTest {

  @Container
  @ServiceConnection
  private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  @Autowired
  private HousingUnitUserMappingRepositoryInterface housingUnitUserMappingRepository;

  @Autowired
  private HousingUnitRepositoryInterface housingUnitRepository;

  @Autowired
  private UserRepositoryInterface userRepository;

  @Autowired
  private BuildingRepositoryInterface buildingRepository;

  @Autowired
  private BuildingUserMappingRepositoryInterface buildingUserMappingRepository;

  @Autowired
  private BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureBuildingMappingRepository;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    housingUnitUserMappingRepository.deleteAll();
    housingUnitRepository.deleteAll();
    buildingUserMappingRepository.deleteAll();
    buildingFeatureBuildingMappingRepository.deleteAll();
    buildingRepository.deleteAll();
  }

  @Test
  void testUpdateBuilding() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setAddress("123 Test St");
    building.setCity("Testville");
    building.setState("TS");
    building.setZipCode("12345");
    building = buildingRepository.save(building);

    String url = String.format(
        "http://localhost:%d/building/%d?city=UpdatedCity&addFeatures=&removeFeatures=",
        port, building.getId()
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<String> request = new HttpEntity<>(headers);

    // Act
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Optional<BuildingEntity> updatedBuilding = buildingRepository.findById(building.getId());
    assertThat(updatedBuilding).isPresent();
    assertThat(updatedBuilding.get().getCity()).isEqualTo("UpdatedCity");
  }

  @Test
  void testCreateBuilding() {
    // Arrange
    String url = "http://localhost:" + port + "/building";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // Clear existing data to ensure no duplicates
    buildingRepository.deleteAll();

    String requestBody = String.format(
        "address=%s&city=%s&state=%s&zipCode=%s&features=",
        "123 Test St",
        "Testville",
        "TS",
        "12345"
    );

    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    // Act
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Optional<BuildingEntity> savedBuilding = buildingRepository.findByAddress("123 Test St");
    assertThat(savedBuilding).isPresent();
    assertThat(savedBuilding.get().getCity()).isEqualTo("Testville");
  }

  @Test
  void testAddBuildingToUser_Success() {
    // Arrange
    UserEntity user = new UserEntity();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmailAddress("john.doe@example.com");
    user = userRepository.save(user);

    BuildingEntity building = new BuildingEntity();
    building.setAddress("123 Test St");
    building.setCity("Testville");
    building.setState("TS");
    building.setZipCode("12345");
    building = buildingRepository.save(building);

    String url = "http://localhost:" + port + "/user/" + user.getId() + "/buildings/" + building.getId();

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Void> request = new HttpEntity<>(headers);

    // Act
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).contains("Building successfully linked to user");

    // Verify the mapping exists
    Optional<BuildingUserMappingEntity> mapping =
        buildingUserMappingRepository.findByUserIdAndBuildingId(user.getId(), building.getId());
    assertThat(mapping).isPresent();
  }

  @Test
  void testAddBuildingToUser_UserNotFound() {
    // Arrange
    int nonExistentUserId = 9999;
    BuildingEntity building = new BuildingEntity();
    building.setAddress("123 Test St");
    building.setCity("Testville");
    building.setState("TS");
    building.setZipCode("12345");
    building = buildingRepository.save(building);

    String url = "http://localhost:" + port + "/user/" + nonExistentUserId + "/buildings/" + building.getId();

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Void> request = new HttpEntity<>(headers);

    // Act
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).contains("User with id " + nonExistentUserId + " not found");
  }

  @Test
  void testAddBuildingToUser_BuildingNotFound() {
    // Arrange
    UserEntity user = new UserEntity();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmailAddress("john.doe@example.com");
    user = userRepository.save(user);

    int nonExistentBuildingId = 9999;

    String url = "http://localhost:" + port + "/user/" + user.getId() + "/buildings/" + nonExistentBuildingId;

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Void> request = new HttpEntity<>(headers);

    // Act
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).contains("Building with id " + nonExistentBuildingId + " not found");
  }

  @Test
  void testAddBuildingToUser_Conflict() {
    // Arrange
    UserEntity user = new UserEntity();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmailAddress("john.doe@example.com");
    user = userRepository.save(user);

    BuildingEntity building = new BuildingEntity();
    building.setAddress("123 Test St");
    building.setCity("Testville");
    building.setState("TS");
    building.setZipCode("12345");
    building = buildingRepository.save(building);

    BuildingUserMappingEntity mapping = new BuildingUserMappingEntity();
    mapping.setUser(user);
    mapping.setBuilding(building);
    mapping.setCreatedDatetime(OffsetDateTime.now());
    mapping.setModifiedDatetime(OffsetDateTime.now());
    buildingUserMappingRepository.save(mapping);

    String url = "http://localhost:" + port + "/user/" + user.getId() + "/buildings/" + building.getId();

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Void> request = new HttpEntity<>(headers);

    // Act
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).contains("This building is already linked to the user");
  }

  @Test
  void testGetAllBuildings_Success() {
    // Arrange
    BuildingEntity building1 = new BuildingEntity();
    building1.setAddress("123 Main St");
    building1.setCity("Testville");
    building1.setState("TS");
    building1.setZipCode("12345");
    buildingRepository.save(building1);

    BuildingEntity building2 = new BuildingEntity();
    building2.setAddress("456 Elm St");
    building2.setCity("SampleCity");
    building2.setState("SC");
    building2.setZipCode("67890");
    buildingRepository.save(building2);

    String url = "http://localhost:" + port + "/buildings";

    // Act
    ResponseEntity<BuildingEntity[]> response = restTemplate.getForEntity(url, BuildingEntity[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  void testGetBuildingsByAddress_Success() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setAddress("123 Main St");
    building.setCity("Testville");
    building.setState("TS");
    building.setZipCode("12345");
    buildingRepository.save(building);

    String url = "http://localhost:" + port + "/buildings?address=123 Main St";

    // Act
    ResponseEntity<BuildingEntity[]> response = restTemplate.getForEntity(url, BuildingEntity[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody()[0].getAddress()).isEqualTo("123 Main St");
  }

  @Test
  void testGetBuildingsByCity_Success() {
    // Arrange
    BuildingEntity building1 = new BuildingEntity();
    building1.setAddress("123 Main St");
    building1.setCity("Testville");
    building1.setState("TS");
    building1.setZipCode("12345");
    buildingRepository.save(building1);

    BuildingEntity building2 = new BuildingEntity();
    building2.setAddress("456 Elm St");
    building2.setCity("Testville");
    building2.setState("TS");
    building2.setZipCode("67890");
    buildingRepository.save(building2);

    String url = "http://localhost:" + port + "/buildings?city=Testville";

    // Act
    ResponseEntity<BuildingEntity[]> response = restTemplate.getForEntity(url, BuildingEntity[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  void testGetBuildingsByState_Success() {
    // Arrange
    BuildingEntity building = new BuildingEntity();
    building.setAddress("123 Main St");
    building.setCity("Testville");
    building.setState("TS");
    building.setZipCode("12345");
    buildingRepository.save(building);

    String url = "http://localhost:" + port + "/buildings?state=TS";

    // Act
    ResponseEntity<BuildingEntity[]> response = restTemplate.getForEntity(url, BuildingEntity[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody()[0].getState()).isEqualTo("TS");
  }

  @Test
  void testGetBuildings_NoContent() {
    // Arrange
    String url = "http://localhost:" + port + "/buildings";

    // Act
    ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void testGetBuildings_NotFound() {
    // Arrange
    String url = "http://localhost:" + port + "/buildings?address=NonExistent";

    // Act
    ResponseEntity<BuildingEntity[]> response = restTemplate.getForEntity(url, BuildingEntity[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
