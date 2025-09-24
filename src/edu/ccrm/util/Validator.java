package edu.ccrm.util;

public class Validator {
    // Example: operator precedence comment
    // int result = a + b * c;  // multiplication (*) has higher precedence than addition (+)

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean isValidRegNo(String regNo) {
        return regNo != null && regNo.matches("S\\d{6,8}");
    }
}
