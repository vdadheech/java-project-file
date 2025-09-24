package edu.ccrm.service;

import edu.ccrm.domain.Student;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StudentService {
    private final Map<String, Student> students = new ConcurrentHashMap<>();

    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public void updateStudent(Student student) {
        students.put(student.getId(), student);
    }

    public void deactivateStudent(String id) {
        Student s = students.get(id);
        if (s != null) {
            s.setStatus("INACTIVE");
        }
    }
}
