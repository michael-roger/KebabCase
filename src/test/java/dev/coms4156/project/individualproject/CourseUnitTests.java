package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.individualproject.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * The unit tests for all methods in the Course.java class
 *
 * @see Course
 */
@SpringBootTest
@ContextConfiguration
public class CourseUnitTests {

  /** Initializes the data for testing before each test runs. */
  @BeforeEach
  public void setupCourseForTesting() {
    testCourse = new Course("Griffin Newbold", "417 IAB", "11:40-12:55", 250);
  }

  @Test
  void instantiationPositivePath() {
    // Test initial values when the Course object is created
    assertEquals("Griffin Newbold", testCourse.getInstructorName());
    assertEquals("417 IAB", testCourse.getCourseLocation());
    assertEquals("11:40-12:55", testCourse.getCourseTimeSlot());
    assertFalse(testCourse.isCourseFull());
  }

  @Test
  void enrollStudentPositivePath() {
    boolean enrolled = testCourse.enrollStudent();
    assertTrue(enrolled);
    assertFalse(testCourse.isCourseFull());
  }

  @Test
  void enrollStudentWhenFull() {
    // Fill up the course by enrolling maximum students
    for (int i = 0; i < 250; i++) {
      testCourse.enrollStudent();
    }
    assertTrue(testCourse.isCourseFull());

    // Try to enroll one more student, should return false
    boolean enrolled = testCourse.enrollStudent();
    assertFalse(enrolled);
  }

  @Test
  void dropStudentPositivePath() {
    // Enroll a student first
    testCourse.enrollStudent();
    assertEquals(1, testCourse.getEnrolledStudentCount());

    // Drop a student
    boolean dropped = testCourse.dropStudent();
    assertTrue(dropped);
    assertEquals(0, testCourse.getEnrolledStudentCount());
  }

  @Test
  void dropStudentDropWhenNoOneIsEnrolled() {
    // Try to drop a student when no one is enrolled
    assertEquals(0, testCourse.getEnrolledStudentCount());

    boolean dropped = testCourse.dropStudent();
    assertFalse(dropped);
  }

  @Test
  void reassignInstructorChangeTheInstructorName() {
    // Test changing the instructor's name
    testCourse.reassignInstructor("Griffin Newbold");
    assertEquals("Griffin Newbold", testCourse.getInstructorName());
  }

  @Test
  void reassignLocationChangingLocation() {
    // Test changing the course location
    testCourse.reassignLocation("417 IAB");
    assertEquals("417 IAB", testCourse.getCourseLocation());
  }

  @Test
  void reassignTimeChangingCourseTime() {
    // Test changing the course time
    testCourse.reassignTime("11:40-12:55");
    assertEquals("11:40-12:55", testCourse.getCourseTimeSlot());
  }

  @Test
  void setEnrolledStudentCountSpecificValue() {
    // Test setting the enrolled student count to a specific value
    testCourse.setEnrolledStudentCount(25);
    assertEquals(25, testCourse.getEnrolledStudentCount());
  }

  @Test
  void setEnrolledStudentCountSetCountToZero() {
    // Test setting the enrolled student count to zero
    testCourse.setEnrolledStudentCount(0);
    assertEquals(0, testCourse.getEnrolledStudentCount());
  }

  @Test
  void setEnrolledStudentCountSetToMaximumCapacity() {
    // Test setting the enrolled student count to the maximum capacity
    testCourse.setEnrolledStudentCount(250);
    assertEquals(250, testCourse.getEnrolledStudentCount());
    assertTrue(testCourse.isCourseFull());
  }

  @Test
  void setEnrolledStudentCountSetToExceedingCapacity() {
    // Test setting the enrolled student count exceeding the capacity
    testCourse.setEnrolledStudentCount(300);
    assertEquals(300, testCourse.getEnrolledStudentCount());
    assertTrue(testCourse.isCourseFull());
  }

  @Test
  void setEnrollmentCapacitySpecificValue() {
    // Test setting the enrollment capacity to a specific value
    int newCapacity = testCourse.setEnrollmentCapacity(100);
    assertEquals(100, newCapacity); // Check the returned value
    assertEquals(100, testCourse.getEnrollmentCapacity()); // Check the internal state
  }

  @Test
  void setEnrollmentCapacitySetToZero() {
    // Test setting the enrollment capacity to zero
    int newCapacity = testCourse.setEnrollmentCapacity(0);
    assertEquals(0, newCapacity); // Check the returned value
    assertEquals(0, testCourse.getEnrollmentCapacity()); // Check the internal state
  }

  @Test
  void setEnrollmentCapacitySetToNegativeValue() {
    // Test setting the enrollment capacity to a negative value
    int newCapacity = testCourse.setEnrollmentCapacity(-10);
    assertEquals(-10, newCapacity); // Check the returned value
    assertEquals(-10, testCourse.getEnrollmentCapacity()); // Check the internal state
  }

  @Test
  void getEnrollmentCapacityPositivePath() {
    // Test the initial enrollment capacity value
    assertEquals(250, testCourse.getEnrollmentCapacity());

    // Set a new capacity and verify the getter retrieves the updated value
    testCourse.setEnrollmentCapacity(200);
    assertEquals(200, testCourse.getEnrollmentCapacity());
  }

  @Test
  public void toStringPositivePath() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("\nInstructor: Griffin Newbold; ");
    stringBuilder.append("Location: 417 IAB; ");
    stringBuilder.append("Time: 11:40-12:55; ");
    stringBuilder.append("Enrolled Student Count: 0; ");

    String expectedResult = stringBuilder.toString();
    assertEquals(expectedResult, testCourse.toString());
  }

  /** The test course instance used for testing. */
  public static Course testCourse;
}
