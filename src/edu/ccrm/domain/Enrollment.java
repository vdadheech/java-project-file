package edu.ccrm.domain;

import java.time.LocalDateTime;

public class Enrollment {
    private String studentId;
    private String courseCode;
    private Grade grade;
    private LocalDateTime enrolledAt;

    public Enrollment(String studentId, String courseCode) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.enrolledAt = LocalDateTime.now();
    }

    // Getters & Setters
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }

    @Override
    public String toString() {
        return String.format("Enrollment[Student: %s, Course: %s, Grade: %s]",
            studentId, courseCode, grade != null ? grade : "N/A");
    }
}
