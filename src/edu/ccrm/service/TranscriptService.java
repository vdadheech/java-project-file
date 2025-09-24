package edu.ccrm.service;

import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TranscriptService {
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    public TranscriptService(EnrollmentService enrollmentService, CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
    }

    public String generateTranscript(String studentId) {
        Student student = enrollmentService.getEnrollmentsForStudent(studentId).isEmpty() ? null : 
            new Student(studentId, "N/A", "N/A", "N/A"); // placeholder; better to inject StudentService

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForStudent(studentId);
        if (enrollments.isEmpty()) {
            return "No enrollments found for student: " + studentId;
        }

        double totalPoints = 0;
        int totalCredits = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== TRANSCRIPT ===\n");
        sb.append("Student ID: ").append(studentId).append("\n");
        sb.append("Course\t\tCredits\tGrade\tPoints\n");
        sb.append("----------------------------------------\n");

        for (Enrollment e : enrollments) {
            var course = courseService.getCourse(e.getCourseCode());
            if (course == null) continue;

            Grade grade = e.getGrade();
            if (grade == null) grade = Grade.F; // ungraded = F

            int credits = course.getCredits();
            int points = grade.getPoints();

            totalPoints += points * credits;
            totalCredits += credits;

            sb.append(String.format("%s\t%d\t%s\t%d\n", 
                e.getCourseCode(), credits, grade, points * credits));
        }

        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
        sb.append("----------------------------------------\n");
        sb.append(String.format("GPA: %.2f (Total Credits: %d)\n", gpa, totalCredits));

        return sb.toString();
    }

    // Stream-based GPA distribution (for reports)
    public Map<Grade, Long> getGpaDistribution() {
        // This is a simplified version; in real app, iterate all students
        return enrollmentService.getEnrollmentsForStudent("dummy")
            .stream()
            .filter(e -> e.getGrade() != null)
            .collect(Collectors.groupingBy(Enrollment::getGrade, Collectors.counting()));
    }
}
