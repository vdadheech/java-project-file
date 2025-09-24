package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CourseService {
    private final Map<String, Course> courses = new ConcurrentHashMap<>();

    public void addCourse(Course course) {
        courses.put(course.getCode().getCode(), course);
    }

    public Course getCourse(String code) {
        return courses.get(code);
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    public List<Course> searchByInstructor(String instructorId) {
        return courses.values().stream()
            .filter(c -> c.getInstructor() != null && c.getInstructor().equals(instructorId))
            .collect(Collectors.toList());
    }

    public List<Course> searchByDepartment(String dept) {
        return courses.values().stream()
            .filter(c -> c.getDepartment().equalsIgnoreCase(dept))
            .collect(Collectors.toList());
    }

    public List<Course> searchBySemester(Semester sem) {
        return courses.values().stream()
            .filter(c -> c.getSemester() == sem)
            .collect(Collectors.toList());
    }

    public void updateCourse(Course course) {
        courses.put(course.getCode().getCode(), course);
    }

    public void deactivateCourse(String code) {
        Course c = courses.get(code);
        if (c != null) {
            // Mark as inactive by setting title to "[INACTIVE] ..."
            c.setTitle("[INACTIVE] " + c.getTitle());
        }
    }
}