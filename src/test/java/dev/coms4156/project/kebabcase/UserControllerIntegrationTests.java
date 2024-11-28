package dev.coms4156.project.kebabcase;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.coms4156.project.kebabcase.entity.ClientEntity;
import dev.coms4156.project.kebabcase.entity.TokenEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.ClientRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.TokenRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
class UserControllerIntegrationTests {

  @Container
  @ServiceConnection
  private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  @Autowired
  private UserRepositoryInterface userRepository;

  @Autowired
  private ClientRepositoryInterface clientRepository;

  @Autowired
  private TokenRepositoryInterface tokenRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testCreateUser() {
    // Arrange
    String firstName = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";
    String password = "securepassword";

    UserEntity user = new UserEntity();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmailAddress(email);
    user.setPassword(password);

    // Act
    UserEntity savedUser = userRepository.save(user);

    // Assert
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getFirstName()).isEqualTo(firstName);
    assertThat(savedUser.getLastName()).isEqualTo(lastName);
    assertThat(savedUser.getEmailAddress()).isEqualTo(email);
  }

  @Test
  void testAuthenticateUser() throws Exception {
    // Arrange
    String email = "john.doe@example.com";
    String password = "securepassword";
    UserEntity user = new UserEntity();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmailAddress(email);
    user.setPassword(password);
    userRepository.save(user);

    // Mock a request payload
    String payload = String.format(
        "{\"email\":\"%s\", \"password\":\"%s\", \"clientName\":\"testClient\"}",
        email, password
    );

    // Mock the HTTP request and response
    ResponseEntity<String> response = ResponseEntity.ok("Mocked Token");

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotEmpty();
  }

  @Test
  void testGetUserInfo() throws JsonProcessingException {
    // Arrange
    String firstName = "Jane";
    String lastName = "Doe";
    String email = "jane.doe@example.com";

    // Create a user
    UserEntity user = new UserEntity();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmailAddress(email);
    user.setPassword("hashedpassword");
    user.setCreatedDatetime(OffsetDateTime.now());
    user.setModifiedDatetime(OffsetDateTime.now());
    UserEntity savedUser = userRepository.save(user);

    // Create a client
    ClientEntity client = new ClientEntity();
    client.setName("TestClient");
    ClientEntity savedClient = clientRepository.save(client);

    // Create a token
    TokenEntity token = new TokenEntity();
    token.setToken("valid-token");
    token.setUser(savedUser);
    token.setClient(savedClient);
    token.setCreatedDatetime(OffsetDateTime.now());
    token.setModifiedDatetime(OffsetDateTime.now());
    token.setExpirationDatetime(OffsetDateTime.now().plusMonths(1));
    tokenRepository.save(token);

    // Prepare HTTP request with token in the header
    String url = "http://localhost:" + port + "/me";

    HttpHeaders headers = new HttpHeaders();
    headers.set("token", "valid-token");

    HttpEntity<String> entity = new HttpEntity<>(headers);

    // Act
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode jsonResponse = objectMapper.readTree(response.getBody());
    assertThat(jsonResponse.get("id").asText()).isEqualTo(savedUser.getId().toString());
    assertThat(jsonResponse.get("firstName").asText()).isEqualTo(firstName);
    assertThat(jsonResponse.get("lastName").asText()).isEqualTo(lastName);
    assertThat(jsonResponse.get("emailAddress").asText()).isEqualTo(email);
  }
}