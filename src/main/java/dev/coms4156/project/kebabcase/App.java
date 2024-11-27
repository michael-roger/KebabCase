package dev.coms4156.project.kebabcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Class contains all the startup logic for the application.
 */
@EntityScan(basePackages = {
    "dev.coms4156.project.kebabcase.entity"
})
@EnableJpaRepositories(basePackages = {
    "dev.coms4156.project.kebabcase.repository",
})
@SpringBootApplication
@EnableTransactionManagement
@SuppressWarnings({"PMD", "checkstyle:hideutilityclassconstructor"})
public class App {

  /**
   * The main launcher for the service all it does is make a call to the overridden run method.
   *
   * @param args A {@code String[]} of any potential runtime arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
