package dev.coms4156.project.kebabcase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ContainerIntegrationTests {

  @Test
  void testContainer() {
    try (GenericContainer<?> container = new GenericContainer<>("alpine:latest")
        .withCommand("echo", "Hello World")) {
      container.start();
      System.out.println(container.getLogs());
    }
  }
}
