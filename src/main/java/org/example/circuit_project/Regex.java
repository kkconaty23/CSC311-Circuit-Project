package org.example.circuit_project;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    Pattern emailPattern;
    Pattern emailMatchPattern;
    Pattern passwordMatchPattern;
    Pattern fNamePattern;
    Pattern lNamePattern;

    Matcher emailMatcher;
    Matcher fNameMatcher;
    Matcher lNameMatcher;
    Matcher emailMatchMatcher;

    public boolean allFieldCheck(String firstName, String lastName, String email) {
        //Only Allow for farmingdale.edu emails
        emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@farmingdale\\.edu$");

        //first and last name must start with a capital
        fNamePattern = Pattern.compile("^[A-Za-z||']{2,25}$");
        lNamePattern = Pattern.compile("^[A-Za-z||']{2,25}$");

        emailMatcher = emailPattern.matcher(email);
        fNameMatcher = fNamePattern.matcher(firstName);
        lNameMatcher = lNamePattern.matcher(lastName);
        return emailMatcher.matches() && fNameMatcher.matches() && lNameMatcher.matches();
    }
    public boolean emailMatchCheck(String email) {
        emailMatchPattern= Pattern.compile(email);
        emailMatcher = emailMatchPattern.matcher(email);
        return emailMatcher.matches();
    }


}
