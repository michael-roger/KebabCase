package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.individualproject.entity.Course;
import dev.coms4156.project.individualproject.entity.Department;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartmentUnitTests {

  /** Initializes the data for testing before each test runs. */
  @BeforeEach
  void setUp() {
    // Initialize courses for testing
    courseMap = new HashMap<>();

    Course coms3827 = new Course("Daniel Rubenstein", "207 Math", "10:10-11:25", 300);

    Course coms4156 = new Course("Gail Kaiser", "501 NWC", "10:10-11:25", 120);

    courseMap.put("3827", coms3827);
    courseMap.put("4156", coms4156);

    // Initialize a Department object before each test
    department = new Department("COMS", courseMap, "Luca Carloni", 2700);
  }

  @Test
  void instantiationPositivePath() {
    // Test that initial state of the department is set correctly
    assertEquals(2700, department.getNumberOfMajors());
    assertEquals("Luca Carloni", department.getDepartmentChair());
    assertEquals(2, department.getCourseSelection().size());
  }

  @Test
  void addPersonToMajorPositivePath() {
    // Test increasing the number of majors
    department.addPersonToMajor();
    assertEquals(2701, department.getNumberOfMajors());
  }

  @Test
  void dropPersonFromMajorPositivePath() {
    // Test decreasing the number of majors
    department.dropPersonFromMajor();
    assertEquals(2699, department.getNumberOfMajors());
  }

  @Test
  void dropPersonFromMajorWhenThereAreZeroPeopleMajoringInThisDepartment() {
    // Set the number of majors to 0 and attempt to drop a person
    department = new Department("ECON", courseMap, "Michael Woodford", 0);
    department.dropPersonFromMajor();

    // Ensure it doesn't go below 0
    assertEquals(0, department.getNumberOfMajors());
  }

  @Test
  void addCoursePositivePath() {
    // Test adding a new course to the department
    Course coms3261 = new Course("Josh Alman", "417 IAB", "2:40-3:55", 150);

    department.addCourse("3261", coms3261);

    Map<String, Course> courses = department.getCourseSelection();
    // Ensure the size of the course map increases
    assertEquals(3, courses.size());

    assertEquals(coms3261, courses.get("3261"));
  }

  @Test
  void createCoursePositivePath() {
    // Test creating and adding a new course using the createCourse method
    department.createCourse("3261", "Josh Alman", "417 IAB", "2:40-3:55", 150);

    Map<String, Course> courses = department.getCourseSelection();
    assertEquals(3, courses.size()); // Ensure the size increases
    assertEquals("Josh Alman", courses.get("3261").getInstructorName());
    assertEquals("417 IAB", courses.get("3261").getCourseLocation());
    assertEquals("2:40-3:55", courses.get("3261").getCourseTimeSlot());
    assertEquals(150, courses.get("3261").getEnrollmentCapacity());
  }

  @Test
  void toStringPositivePath() {
    // Test the string representation of the department
    String departmentString = department.toString();
    // Ensure the course entries are present
    assertTrue(departmentString.contains("COMS 3827:"));
    assertTrue(departmentString.contains("COMS 4156:"));
  }

  private Department department;
  private Map<String, Course> courseMap;
}
