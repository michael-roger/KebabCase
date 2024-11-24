package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import dev.coms4156.project.kebabcase.controller.UserController;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerUnitTests {

  @Mock
  private UserRepositoryInterface userRepository;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    // Instantiate the controller before each test
    MockitoAnnotations.openMocks(this);
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

}

