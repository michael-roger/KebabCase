package dev.coms4156.project.kebabcase;

import dev.coms4156.project.kebabcase.entity.UserEntity;
import dev.coms4156.project.kebabcase.repository.UserRepositoryInterface;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.assertj.core.api.Assertions;

@SpringBootTest
@Transactional // Optional: Rollback transactions after each test
class UserRepositoryIntegrationTest {

  @Autowired
  private UserRepositoryInterface userRepository;

  @Test
  @Disabled("Reason for skipping")
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
