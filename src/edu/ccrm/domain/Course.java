package edu.ccrm.domain;

import edu.ccrm.domain.immutable.CourseCode;

public class Course {
    private final CourseCode code;
    private String title;
    private int credits;
    private String instructor; // instructor ID
    private Semester semester;
    private String department;

    private Course(Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.credits = builder.credits;
        this.instructor = builder.instructor;
        this.semester = builder.semester;
        this.department = builder.department;
    }

    // Getters
    public CourseCode getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public Semester getSemester() { return semester; }
    public String getDepartment() { return department; }

    // Setters (for update)
    public void setTitle(String title) { this.title = title; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public void setSemester(Semester semester) { this.semester = semester; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return String.format("Course[%s: %s, %d credits, Instructor: %s, %s, %s]",
            code, title, credits, instructor, semester, department);
    }

    // Static Builder (nested static class)
    public static class Builder {
        private CourseCode code;
        private String title;
        private int credits;
        private String instructor;
        private Semester semester;
        private String department;

        public Builder code(CourseCode code) {
            this.code = code;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder credits(int credits) {
            if (credits < 1 || credits > 12) {
                throw new IllegalArgumentException("Credits must be between 1 and 12");
            }
            this.credits = credits;
            return this;
        }

        public Builder instructor(String instructor) {
            this.instructor = instructor;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Course build() {
            if (code == null || title == null || department == null) {
                throw new IllegalStateException("Code, title, and department are required");
            }
            return new Course(this);
        }
    }
}