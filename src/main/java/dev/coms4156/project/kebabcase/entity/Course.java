package dev.coms4156.project.kebabcase.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class acts as an Object that represents a school course Examples of information in this
 * object include course location, instructor name, and the number of enrolled students.
 */
public class Course implements Serializable {

  /**
   * Constructs a new Course object with the given parameters. Initial count starts at 0.
   *
   * @param instructorName The name of the instructor teaching the course.
   * @param courseLocation The location where the course is held.
   * @param timeSlot The time slot of the course.
   * @param capacity The maximum number of students that can enroll in the course.
   */
  public Course(String instructorName, String courseLocation, String timeSlot, int capacity) {
    this.courseLocation = courseLocation;
    this.instructorName = instructorName;
    this.courseTimeSlot = timeSlot;
    this.enrollmentCapacity = capacity;
    this.enrolledStudentCount = 0;
  }

  /**
   * Enrolls a student in the course if there is space available.
   *
   * @return true if the student is successfully enrolled, false otherwise.
   */
  public boolean enrollStudent() {
    if (!this.isCourseFull()) {
      enrolledStudentCount++;
      return true;
    }
    return false;
  }

  /**
   * Drops a student from the course if a student is enrolled.
   *
   * @return true if the student is successfully dropped, false otherwise.
   */
  public boolean dropStudent() {
    if (this.enrolledStudentCount > 0) {
      enrolledStudentCount--;
      return true;
    }

    return false;
  }

  public String getCourseLocation() {
    return this.courseLocation;
  }

  public String getInstructorName() {
    return this.instructorName;
  }

  public String getCourseTimeSlot() {
    return this.courseTimeSlot;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("\n");
    stringBuilder.append("Instructor: " + this.instructorName + "; ");
    stringBuilder.append("Location: " + this.courseLocation + "; ");
    stringBuilder.append("Time: " + this.courseTimeSlot + "; ");
    stringBuilder.append("Enrolled Student Count: " + this.enrolledStudentCount + "; ");

    return stringBuilder.toString();
  }

  public void reassignInstructor(String newInstructorName) {
    this.instructorName = newInstructorName;
  }

  public void reassignLocation(String newLocation) {
    this.courseLocation = newLocation;
  }

  public void reassignTime(String newTime) {
    this.courseTimeSlot = newTime;
  }

  public void setEnrolledStudentCount(int count) {
    this.enrolledStudentCount = count;
  }

  public int getEnrolledStudentCount() {
    return this.enrolledStudentCount;
  }

  public int setEnrollmentCapacity(int enrollmentCapacity) {
    return this.enrollmentCapacity = enrollmentCapacity;
  }

  public int getEnrollmentCapacity() {
    return this.enrollmentCapacity;
  }

  public boolean isCourseFull() {
    return this.enrolledStudentCount >= this.enrollmentCapacity;
  }

  @Serial private static final long serialVersionUID = 123456L;
  private int enrollmentCapacity;
  private int enrolledStudentCount;
  private String courseLocation;
  private String instructorName;
  private String courseTimeSlot;
}
