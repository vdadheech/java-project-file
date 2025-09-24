package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Student;
import edu.ccrm.domain.immutable.CourseCode;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.StudentService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ImportExportService {
    private final StudentService studentService;
    private final CourseService courseService;
    private final Path dataDir;

    public ImportExportService(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.dataDir = AppConfig.getInstance().getDataDir();
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create data directory", e);
        }
    }

    public void exportStudents() {
        Path file = dataDir.resolve("students.csv");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
            pw.println("id,regNo,fullName,email,status");
            studentService.getAllStudents().forEach(s -> {
                pw.printf("%s,%s,%s,%s,%s%n",
                    s.getId(), s.getRegNo(), s.getName(), s.getEmail(), s.getStatus());
            });
        } catch (IOException e) {
            throw new RuntimeException("Export failed", e);
        }
    }

    public void exportCourses() {
        Path file = dataDir.resolve("courses.csv");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
            pw.println("code,title,credits,instructor,semester,department");
            courseService.getAllCourses().forEach(c -> {
                pw.printf("%s,%s,%d,%s,%s,%s%n",
                    c.getCode(), c.getTitle(), c.getCredits(),
                    c.getInstructor(), c.getSemester(), c.getDepartment());
            });
        } catch (IOException e) {
            throw new RuntimeException("Export failed", e);
        }
    }

    public void importStudents() {
        Path file = dataDir.resolve("students.csv");
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            br.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Student s = new Student(parts[0], parts[1], parts[2], parts[3]);
                    s.setStatus(parts[4]);
                    studentService.addStudent(s);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Import failed", e);
        }
    }

    public void importCourses() {
        Path file = dataDir.resolve("courses.csv");
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            br.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        Course course = new Course.Builder()
                            .code(new CourseCode(parts[0]))
                            .title(parts[1])
                            .credits(Integer.parseInt(parts[2]))
                            .instructor(parts[3])
                            .semester(edu.ccrm.domain.Semester.valueOf(parts[4].toUpperCase()))
                            .department(parts[5])
                            .build();
                        courseService.addCourse(course);
                    } catch (Exception e) {
                        System.err.println("Skipping invalid course: " + line);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Import failed", e);
        }
    }
}