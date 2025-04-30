package org.example.circuit_project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    // Email pattern for @farmingdale.edu
    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@farmingdale\\.edu$");

    // Name patterns - letters and apostrophes only, 2-25 chars
    private final Pattern namePattern = Pattern.compile("^[A-Za-z']{2,25}$");

    // Password pattern - only checking for non-whitespace (can be extended)
    private final Pattern passwordPattern = Pattern.compile("^\\S+$");

    /**
     * Checks if all form fields meet regex requirements
     *
     * @param firstName First name to validate
     * @param lastName Last name to validate
     * @param email Email to validate
     * @param password Password to validate
     * @return true if all fields meet requirements
     */
    public boolean Regex(String firstName, String lastName, String email, String password) {
        return firstNameCheck(firstName) &&
                lastNameCheck(lastName) &&
                emailCheck(email) &&
                password != null && !password.isEmpty();
    }

    /**
     * Validates first name format
     *
     * @param firstName First name to validate
     * @return true if valid, false otherwise
     */
    public boolean firstNameCheck(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return false;
        }
        Matcher matcher = namePattern.matcher(firstName);
        return matcher.matches();
    }

    /**
     * Validates last name format
     *
     * @param lastName Last name to validate
     * @return true if valid, false otherwise
     */
    public boolean lastNameCheck(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return false;
        }
        Matcher matcher = namePattern.matcher(lastName);
        return matcher.matches();
    }

    /**
     * Validates email format (@farmingdale.edu)
     *
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public boolean emailCheck(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validates that two emails match
     *
     * @param email Original email
     * @param confirmEmail Confirmation email
     * @return true if they match
     */
    public boolean emailMatchCheck(String email, String confirmEmail) {
        return email != null && email.equals(confirmEmail);
    }

    /**
     * Validates that two passwords match
     *
     * @param password Original password
     * @param confirmPassword Confirmation password
     * @return true if they match
     */
    public boolean passwordMatchCheck(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}