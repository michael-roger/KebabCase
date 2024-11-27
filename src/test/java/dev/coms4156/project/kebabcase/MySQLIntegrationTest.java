package dev.coms4156.project.kebabcase;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class MySQLIntegrationTest {

  @Container
  @ServiceConnection
  private static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  // Example test case
  @org.junit.jupiter.api.Test
  void contextLoads() {
    System.out.println("MySQL JDBC URL: " + mysqlContainer.getJdbcUrl());
    System.out.println("MySQL Username: " + mysqlContainer.getUsername());
    System.out.println("MySQL Password: " + mysqlContainer.getPassword());
  }
}
