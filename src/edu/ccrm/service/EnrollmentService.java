package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Student;
import edu.ccrm.exception.DuplicateEnrollment;
import edu.ccrm.exception.MaxCreditLimitExceededException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final Map<String, List<Enrollment>> studentEnrollments = new ConcurrentHashMap<>();
    private final StudentService studentService;
    private final CourseService courseService;
    private static final int MAX_CREDITS_PER_SEMESTER = 18;

    public EnrollmentService(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
    }

    public void enrollStudent(String studentId, String courseCode) throws DuplicateEnrollment, MaxCreditLimitExceededException {
        Student student = studentService.getStudent(studentId);
        Course course = courseService.getCourse(courseCode);

        if (student == null || course == null) {
            throw new IllegalArgumentException("Student or course not found");
        }

        if ("INACTIVE".equals(student.getStatus())) {
            throw new IllegalStateException("Cannot enroll inactive student");
        }

        List<Enrollment> enrollments = studentEnrollments.computeIfAbsent(studentId, k -> new ArrayList<>());

        if (enrollments.stream().anyMatch(e -> e.getCourseCode().equals(courseCode))) {
            throw new DuplicateEnrollment("Student already enrolled in course: " + courseCode);
        }

        // Check credit limit
        int totalCredits = enrollments.stream()
            .map(Enrollment::getCourseCode)
            .map(courseService::getCourse)
            .filter(Objects::nonNull)
            .mapToInt(Course::getCredits)
            .sum() + course.getCredits();

        if (totalCredits > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(
                "Enrollment would exceed max credit limit of " + MAX_CREDITS_PER_SEMESTER);
        }

        enrollments.add(new Enrollment(studentId, courseCode));
    }

    public void unenrollStudent(String studentId, String courseCode) {
        List<Enrollment> enrollments = studentEnrollments.get(studentId);
        if (enrollments != null) {
            enrollments.removeIf(e -> e.getCourseCode().equals(courseCode));
        }
        // Also remove from student's enrolledCourses list
        Student s = studentService.getStudent(studentId);
        if (s != null) {
            s.removeCourse(courseCode);
        }
    }

    public List<Enrollment> getEnrollmentsForStudent(String studentId) {
        return new ArrayList<>(studentEnrollments.getOrDefault(studentId, new ArrayList<>()));
    }

    public void recordGrade(String studentId, String courseCode, edu.ccrm.domain.Grade grade) {
        List<Enrollment> enrollments = studentEnrollments.get(studentId);
        if (enrollments != null) {
            for (Enrollment e : enrollments) {
                if (e.getCourseCode().equals(courseCode)) {
                    e.setGrade(grade);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Enrollment not found");
    }
}
