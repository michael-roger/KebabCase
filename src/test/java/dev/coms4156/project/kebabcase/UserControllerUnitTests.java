package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import dev.coms4156.project.kebabcase.controller.UserController;
import dev.coms4156.project.kebabcase.entity.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;

class UserControllerUnitTests {

  @Mock
  private UserRepositoryInterface userRepository;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAuthenticate_Success() {
    // Arrange
    String email = "test@example.com";
    String password = "password123";
    UserEntity user = new UserEntity();
    user.setId(1);
    user.setEmailAddress(email);
    user.setPassword(password);

    when(userRepository.findByEmailAddress(email)).thenReturn(Optional.of(user));

    // Act
    ResponseEntity<Integer> response = userController.authenticate(email, password);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(user.getId(), response.getBody());
    verify(userRepository, times(1)).findByEmailAddress(email);
  }

  @Test
  void testAuthenticate_EmailNotFound() {
    // Arrange
    String email = "missing@example.com";
    String password = "password123";

    when(userRepository.findByEmailAddress(email)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<Integer> response = userController.authenticate(email, password);

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
    UserEntity user = new UserEntity();
    user.setId(1);
    user.setEmailAddress(email);
    user.setPassword(correctPassword);

    when(userRepository.findByEmailAddress(email)).thenReturn(Optional.of(user));

    // Act
    ResponseEntity<Integer> response = userController.authenticate(email, wrongPassword);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNull(response.getBody());
    verify(userRepository, times(1)).findByEmailAddress(email);
  }

  @Test
  void testAuthenticate_BlankEmailOrPassword() {
    // Act
    ResponseEntity<Integer> responseWithBlankEmail = userController.authenticate("", "password123");
    ResponseEntity<Integer> responseWithBlankPassword = userController.authenticate("test@example.com", "");

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, responseWithBlankEmail.getStatusCode());
    assertNull(responseWithBlankEmail.getBody());

    assertEquals(HttpStatus.BAD_REQUEST, responseWithBlankPassword.getStatusCode());
    assertNull(responseWithBlankPassword.getBody());

    verifyNoInteractions(userRepository);
  }
}
