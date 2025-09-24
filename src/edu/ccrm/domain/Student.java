package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person {
    private String regNo;
    private String status; // "ACTIVE", "INACTIVE"
    private List<String> enrolledCourses; // store course codes
    private LocalDateTime lastUpdated;

    public Student(String id, String regNo, String fullName, String email) {
        super(id, fullName, email);
        this.regNo = regNo;
        this.status = "ACTIVE";
        this.enrolledCourses = new ArrayList<>();
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters & Setters
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public List<String> getEnrolledCourses() { return new ArrayList<>(enrolledCourses); }
    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = new ArrayList<>(enrolledCourses);
        this.lastUpdated = LocalDateTime.now();
    }

    public void addCourse(String courseCode) {
        if (!enrolledCourses.contains(courseCode)) {
            enrolledCourses.add(courseCode);
            lastUpdated = LocalDateTime.now();
        }
    }

    public void removeCourse(String courseCode) {
        enrolledCourses.remove(courseCode);
        lastUpdated = LocalDateTime.now();
    }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Student[ID: %s, RegNo: %s, Name: %s, Status: %s, Courses: %d]",
            id, regNo, Name, status, enrolledCourses.size());
    }
}