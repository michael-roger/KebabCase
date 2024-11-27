package dev.coms4156.project.kebabcase;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MySQLContainerTest {

  @Test
  void testContainerStarts() {
    try (MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")) {
      mysql.start();
      assertTrue(mysql.isRunning());
    }
  }
}