package dev.coms4156.project.kebabcase;

import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@Transactional // Optional: Rollback transactions after each test
class UserRepositoryIntegrationTest {

  @Container
  @ServiceConnection
  private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");
  
  @Autowired
  private UserRepositoryInterface userRepository;

  @Test
  void testSaveUser() {
    UserEntity user = new UserEntity();
    user.setFirstName("Homer");
    user.setLastName("Simpson");
    user.setEmailAddress("homer@simpson.com");

    UserEntity savedUser = userRepository.save(user);

    Assertions.assertThat(savedUser.getId()).isNotNull();
    Assertions.assertThat(savedUser.getFirstName()).isEqualTo("Homer");
    Assertions.assertThat(savedUser.getLastName()).isEqualTo("Simpson");
    Assertions.assertThat(savedUser.getEmailAddress()).isEqualTo("homer@simpson.com");
  }
}
