package dev.coms4156.project.kebabcase;

import dev.coms4156.project.kebabcase.entity.Department;
import dev.coms4156.project.kebabcase.service.DatabaseService;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class contains all the startup logic for the application.
 *
 * <p>DO NOT MODIFY ANYTHING BELOW THIS POINT WITH REGARD TO FUNCTIONALITY YOU MAY MAKE
 * STYLE/REFACTOR MODIFICATIONS AS NEEDED
 */
@SpringBootApplication
public class App implements CommandLineRunner {

  /**
   * The main launcher for the service all it does is make a call to the overridden run method.
   *
   * @param args A {@code String[]} of any potential runtime arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  /**
   * Overrides the database reference, used when testing.
   *
   * @param testData A {@code MyFileDatabase} object referencing test data.
   */
  public static void overrideDatabase(MyFileDatabase testData) {
    myFileDatabase = testData;
    saveData = false;
  }

  /**
   * This contains all the setup logic, it will mainly be focused on loading up and creating an
   * instance of the database based off a saved file or will create a fresh database if the file is
   * not present.
   *
   * @param args A {@code String[]} of any potential runtime args
   */
  @Override
  public void run(String[] args) {
    for (String arg : args) {
      if ("setup".equals(arg)) {
        myFileDatabase = DatabaseService.resetDataFile();
        System.out.println("System Setup");
        return;
      }
    }

    myFileDatabase = new MyFileDatabase();
    Map<String, Department> databaseMapping = DatabaseService.createDatabaseMapping();
    myFileDatabase.setMapping(databaseMapping);

    System.out.println("Start up");
  }

  /**
   * This contains all the overheading teardown logic, it will mainly be focused on saving all the
   * created user data to a file, so it will be ready for the next setup.
   */
  @PreDestroy
  public void onTermination() {
    System.out.println("Termination");
    if (saveData) {
      myFileDatabase.saveContentsToFile();
    }
  }

  public static MyFileDatabase myFileDatabase;

  private static boolean saveData = true;
}
