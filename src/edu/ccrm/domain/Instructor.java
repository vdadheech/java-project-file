package edu.ccrm.domain;

public class Instructor extends Person {
    private String department;

    public Instructor(String id, String fullName, String email, String department) {
        super(id, fullName, email);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getRole() { return "Instructor"; }

    @Override
    public String toString() {
        return String.format("Instructor[ID: %s, Name: %s, Dept: %s]", id, Name, department);
    }
}