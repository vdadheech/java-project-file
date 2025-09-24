package edu.ccrm.util;

import edu.ccrm.domain.Student;

import java.util.Comparator;

public class Comparators {
    public static final Comparator<Student> BY_NAME = (s1, s2) -> 
        s1.getName().compareToIgnoreCase(s2.getName());

    public static final Comparator<Student> BY_REGNO = Comparator.comparing(Student::getRegNo);
}