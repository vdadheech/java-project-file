package edu.ccrm.domain;

import java.time.LocalDateTime;

public abstract class Person {
    protected String id;
    protected String Name;
    protected String email;
    protected LocalDateTime createdAt;

    public Person(String id, String fullName, String email) {
        this.id = id;
        this.Name = fullName;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return Name; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Abstract method
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s [ID: %s, Name: %s, Email: %s]", 
            getRole(), id, Name, email);
    }
}