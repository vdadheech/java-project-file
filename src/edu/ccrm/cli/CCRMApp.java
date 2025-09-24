package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.Semester;
import edu.ccrm.domain.Student;
import edu.ccrm.exception.DuplicateEnrollment;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.io.BackupService;
import edu.ccrm.io.ImportExportService;
import edu.ccrm.service.*;
import edu.ccrm.util.FileSizeCalculator;
import java.nio.file.Path; 

import java.util.Scanner;

public class CCRMApp {
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService(studentService, courseService);
    private final TranscriptService transcriptService = new TranscriptService(enrollmentService, courseService);
    private final ImportExportService ioService = new ImportExportService(studentService, courseService);
    private final BackupService backupService = new BackupService();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Print Java platform note
        System.out.println("=== Campus Course & Records Manager (CCRM) ===");
        System.out.println("Built on Java SE (Standard Edition) for desktop/CLI applications.");
        System.out.println("Java ME: Micro Edition (embedded), Java EE: Enterprise Edition (web servers - now Jakarta EE)\n");

        new CCRMApp().start();
    }

    public void start() {
        // Load config (singleton)
        AppConfig config = AppConfig.getInstance();
        System.out.println("Data directory: " + config.getDataDir().toAbsolutePath());

        // Sample data
        addSampleData();

        mainLoop: while (true) {
            showMenu();
            int choice = getIntInput();
            switch (choice) {
                case 1 -> manageStudents();
                case 2 -> manageCourses();
                case 3 -> manageEnrollment();
                case 4 -> manageGrades();
                case 5 -> {
                    ioService.exportStudents();
                    ioService.exportCourses();
                    System.out.println("Data exported to 'data/' folder.");
                }
                case 6 -> {
                    ioService.importStudents();
                    ioService.importCourses();
                    System.out.println("Data imported from 'data/' folder.");
                }
                case 7 -> {
                    Path backup = backupService.backup();
                    long size = FileSizeCalculator.computeTotalSize(backup);
                    System.out.printf("Backup size: %d bytes\n", size);
                }
                case 8 -> {
                    System.out.println(transcriptService.getGpaDistribution());
                }
                case 0 -> {
                    System.out.println("Thank you for using CCRM!");
                    break mainLoop; // labeled break
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }

    private void showMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Enroll/Unenroll");
        System.out.println("4. Record Grades");
        System.out.println("5. Export Data");
        System.out.println("6. Import Data");
        System.out.println("7. Backup & Show Size");
        System.out.println("8. Reports (GPA Distribution)");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void manageStudents() {
        System.out.println("1. Add Student\n2. List Students\n3. Deactivate Student");
        int opt = getIntInput();
        if (opt == 1) {
            System.out.print("ID: "); String id = scanner.next();
            System.out.print("RegNo (e.g., S2024001): "); String reg = scanner.next();
            System.out.print("Full Name: "); String name = scanner.next();
            System.out.print("Email: "); String email = scanner.next();
            studentService.addStudent(new Student(id, reg, name, email));
            System.out.println("Student added.");
        } else if (opt == 2) {
            studentService.getAllStudents().forEach(System.out::println);
        } else if (opt == 3) {
            System.out.print("Student ID to deactivate: ");
            studentService.deactivateStudent(scanner.next());
        }
    }

    private void manageCourses() {
        System.out.println("1. Add Course\n2. List Courses\n3. Search by Department");
        int opt = getIntInput();
        if (opt == 1) {
            System.out.print("Code: "); String code = scanner.next();
            System.out.print("Title: "); String title = scanner.next();
            System.out.print("Credits: "); int credits = scanner.nextInt();
            System.out.print("Instructor ID: "); String inst = scanner.next();
            System.out.print("Semester (SPRING/SUMMER/FALL): "); 
            Semester sem = Semester.valueOf(scanner.next().toUpperCase());
            System.out.print("Department: "); String dept = scanner.next();
            try {
                var course = new edu.ccrm.domain.Course.Builder()
                    .code(new edu.ccrm.domain.immutable.CourseCode(code))
                    .title(title)
                    .credits(credits)
                    .instructor(inst)
                    .semester(sem)
                    .department(dept)
                    .build();
                courseService.addCourse(course);
                System.out.println("Course added.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else if (opt == 2) {
            courseService.getAllCourses().forEach(System.out::println);
        } else if (opt == 3) {
            System.out.print("Department: ");
            courseService.searchByDepartment(scanner.next()).forEach(System.out::println);
        }
    }

    private void manageEnrollment() {
    System.out.print("Student ID: ");
    String sid = scanner.next();
    System.out.print("Course Code: ");
    String cc = scanner.next();
    System.out.print("1. Enroll  2. Unenroll: ");
    
    if (scanner.nextInt() == 1) {
        try {
            enrollmentService.enrollStudent(sid, cc);
            // Optional: update student's course list for display
            Student s = studentService.getStudent(sid);
            if (s != null) {
                s.addCourse(cc);
            }
            System.out.println("✅ Successfully enrolled!");
        } catch (DuplicateEnrollment e) {
            System.out.println("❌ " + e.getMessage());
        } catch (MaxCreditLimitExceededException e) {
            System.out.println("❌ " + e.getMessage());
        }
    } else {
        enrollmentService.unenrollStudent(sid, cc);
        System.out.println("✅ Unenrolled successfully.");
    }
}

    private void manageGrades() {
        System.out.print("Student ID: "); String sid = scanner.next();
        System.out.print("Course Code: "); String cc = scanner.next();
        System.out.print("Grade (S,A,B,C,D,E,F): ");
        try {
            edu.ccrm.domain.Grade grade = edu.ccrm.domain.Grade.valueOf(scanner.next().toUpperCase());
            enrollmentService.recordGrade(sid, cc, grade);
            System.out.println("Grade recorded.");
        } catch (Exception e) {
            System.out.println("Invalid grade.");
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.next());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addSampleData() {
        // Add sample student
        studentService.addStudent(new Student("1", "S2024001", "Alice Smith", "alice@uni.edu"));
        // Add sample course
        try {
            var course = new edu.ccrm.domain.Course.Builder()
                .code(new edu.ccrm.domain.immutable.CourseCode("CS101"))
                .title("Intro to Java")
                .credits(3)
                .instructor("INST1")
                .semester(Semester.FALL)
                .department("Computer Science")
                .build();
            courseService.addCourse(course);
        } catch (Exception ignored) {}
    }
}