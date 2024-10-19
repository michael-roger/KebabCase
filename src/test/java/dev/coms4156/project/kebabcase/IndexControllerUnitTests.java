package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.kebabcase.controller.IndexController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexControllerUnitTests {

  private IndexController indexController;

  @BeforeEach
  void setUp() {
    // Instantiate the controller before each test
    indexController = new IndexController();
  }

  @Test
  void testIndexEndpointSuccess() {
    // Act
    String result = indexController.index();

    // Assert
    assertEquals("Welcome!", result);  // Expect the result to be "Welcome!"
  }
}
