package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.coms4156.project.kebabcase.entity.ClientEntity;
import dev.coms4156.project.kebabcase.repository.ClientRepositoryInterface;
import dev.coms4156.project.kebabcase.entity.TokenEntity;
import dev.coms4156.project.kebabcase.repository.TokenRepositoryInterface;
import dev.coms4156.project.kebabcase.controller.UserController;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@AutoConfigureMockMvc
class UserControllerUnitTests {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private UserRepositoryInterface userRepository;

  @Mock
  private ClientRepositoryInterface clientRepository;

  @Mock
  private TokenRepositoryInterface tokenRepository;

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  private String hashPassword(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : encodedHash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing password", e);
    }
  }

  @Test
  void testAuthenticate_Success() {
    // Arrange
    String email = "test@example.com";
    String password = "password123";
    String hashedPassword = hashPassword(password);
    String clientName = "testClientName";
    String tokenValue = "testTokenValue";

    ClientEntity client = new ClientEntity();
    client.setId(1);
    client.setName(clientName);

    UserEntity user = new UserEntity();
    user.setId(1);
    user.setEmailAddress(email);
    user.setPassword(hashedPassword);

    TokenEntity token = new TokenEntity();
    token.setId(1);
    token.setToken(tokenValue);
    token.setClient(client);

    when(clientRepository.findByName(clientName)).thenReturn(Optional.of(client));

    when(userRepository.findByEmailAddress(email)).thenReturn(Optional.of(user));

    when(tokenRepository.save(any(TokenEntity.class))).thenReturn(token);

    // Act
    ResponseEntity<String> response = userController.authenticate(
        email,
        password,
        clientName
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userRepository, times(1)).findByEmailAddress(email);
    verify(clientRepository, times(1)).findByName(clientName);
  }

  @Test
  void testAuthenticate_EmailNotFound() {
    // Arrange
    String email = "missing@example.com";
    String password = "password123";
    String clientName = "testClientName";

    ClientEntity client = new ClientEntity();
    client.setId(1);
    client.setName(clientName);

    when(clientRepository.findByName(clientName)).thenReturn(Optional.of(client));

    when(userRepository.findByEmailAddress(email)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = userController.authenticate(email, password, clientName);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(userRepository, times(1)).findByEmailAddress(email);
  }

  @Test
  void testAuthenticate_InvalidPassword() {
    // Arrange
    String email = "test@example.com";
    String correctPassword = "password123";
    String wrongPassword = "wrongpassword";
    String hashedCorrectPassword = hashPassword(correctPassword);
    String clientName = "testClientName";

    ClientEntity client = new ClientEntity();
    client.setId(1);
    client.setName(clientName);

    when(clientRepository.findByName(clientName)).thenReturn(Optional.of(client));

    UserEntity user = new UserEntity();
    user.setId(1);
    user.setEmailAddress(email);
    user.setPassword(hashedCorrectPassword);

    when(userRepository.findByEmailAddress(email)).thenReturn(Optional.of(user));

    // Act
    ResponseEntity<String> response = userController.authenticate(email, wrongPassword, clientName);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNull(response.getBody());
    verify(userRepository, times(1)).findByEmailAddress(email);
  }

  @Test
  void testAuthenticate_BlankEmailOrPassword() {
    String clientName = "testClientName";

    ClientEntity client = new ClientEntity();
    client.setId(1);
    client.setName(clientName);

    when(clientRepository.findByName(clientName)).thenReturn(Optional.of(client));

    // Act
    ResponseEntity<String> responseWithBlankEmail = userController.authenticate(
        "",
        "password123",
        clientName
    );

    ResponseEntity<String> responseWithBlankPassword = userController.authenticate(
        "test@example.com",
        "",
        clientName
    );

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, responseWithBlankEmail.getStatusCode());
    assertNull(responseWithBlankEmail.getBody());

    assertEquals(HttpStatus.BAD_REQUEST, responseWithBlankPassword.getStatusCode());
    assertNull(responseWithBlankPassword.getBody());

    verifyNoInteractions(userRepository);
  }

  @Test
  void testCreateUserSuccess() {
    String firstName = "Sue";
    String lastName = "Donym";
    String emailAddress = "notapseudonym@example.com";
    String password = "alias?notI";

    UserEntity user = new UserEntity();
    user.setId(100);

    when(userRepository.findByEmailAddress(
            emailAddress))
            .thenReturn(Optional.empty());

    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    ResponseEntity<?> response = userController.createUser(firstName,
            lastName, emailAddress, password);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains(
            "User was added successfully! User ID: "));
    verify(userRepository, times(1)).save(any(UserEntity.class));
  }

  @Test
  void testCreateUserFailure() {
    String firstName = "Emily";
    String lastName = "Johnson";
    String emailAddress = "emily.johnson@example.com";
    String password = "password789";

    UserEntity user = new UserEntity();
    user.setEmailAddress(emailAddress);

    when(userRepository.findByEmailAddress(
            emailAddress))
            .thenReturn(Optional.of(user));

    ResponseEntity<?> response = userController.createUser(firstName,
            lastName, emailAddress, password);



    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertTrue(response.getBody().toString().contains(
            "There is an account already associated with "));
  }
  
  @Test
  public void testGetUserInfo_Success() throws Exception {
    // Arrange
    String tokenString = "valid-token";

    UserEntity user = new UserEntity();
    user.setId(1);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmailAddress("john.doe@example.com");

    TokenEntity token = new TokenEntity();
    token.setToken(tokenString);
    token.setUser(user);

    // Mock token repository to return the token
    when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

    // Mock ObjectNode to simulate ObjectMapper's createObjectNode behavior
    ObjectNode mockNode = mock(ObjectNode.class);

    when(objectMapper.createObjectNode()).thenReturn(mockNode);
    when(mockNode.put(eq("id"), eq(user.getId()))).thenReturn(mockNode);
    when(mockNode.put(eq("firstName"), eq(user.getFirstName()))).thenReturn(mockNode);
    when(mockNode.put(eq("lastName"), eq(user.getLastName()))).thenReturn(mockNode);
    when(mockNode.put(eq("emailAddress"), eq(user.getEmailAddress()))).thenReturn(mockNode);

    // Act
    ResponseEntity<?> response = userController.getUserInfo(new MockHttpServletRequest() {
        @Override
        public String getHeader(String name) {
            if ("token".equals(name)) {
                return tokenString;
            }
            return null;
        }
    });

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockNode, response.getBody());
    verify(tokenRepository, times(1)).findByToken(tokenString);
    verify(objectMapper, times(1)).createObjectNode();
    verify(mockNode, times(1)).put("id", user.getId());
    verify(mockNode, times(1)).put("firstName", user.getFirstName());
    verify(mockNode, times(1)).put("lastName", user.getLastName());
    verify(mockNode, times(1)).put("emailAddress", user.getEmailAddress());
  }

  @Test
  public void testGetUserInfo_TokenNotFound() throws Exception {
    // Arrange
    String tokenString = "invalid-token";

    when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/me")
            .header("token", tokenString))
            .andExpect(status().isNotFound());
  }

  @Test
  public void testGetUserInfo_MissingTokenHeader() throws Exception {
    // Act & Assert
    mockMvc.perform(get("/me"))
        .andExpect(status().isUnauthorized());
  }
}
