package dev.coms4156.project.kebabcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.kebabcase.controller.WarmupController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarmupControllerUnitTests {

  private WarmupController warmupController;

  @BeforeEach
  void setUp() {
    // Instantiate the controller before each test
    warmupController = new WarmupController();
  }

  @Test
  void testIndexEndpointSuccess() {
    // Act
    String result = warmupController.warmup();

    // Assert
    assertEquals("Warmup!", result); // Expect the result to be "Warmup!"
  }
}
