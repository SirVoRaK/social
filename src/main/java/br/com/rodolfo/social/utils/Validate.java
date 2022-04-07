package br.com.rodolfo.social.utils;

import br.com.rodolfo.social.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
    public static final String emailExample = "example@example.com";
    public static final String passwordExample = "Password@123";

    public static boolean email(String email) {
        return Pattern.matches("[a-zA-Z_.0-9]+@[a-zA-Z_.0-9]+\\.[a-zA-Z_.0-9]+", email);
    }

    public static boolean password(String password) {
        if (password.length() < UserService.passwordMinLength) return false;
        if (password.length() > UserService.passwordMaxLength) return false;
        Matcher upperCaseMatcher = Pattern.compile("[A-Z]+").matcher(password);
        Matcher lowerCaseMatcher = Pattern.compile("[a-z]+").matcher(password);
        Matcher numberMatcher = Pattern.compile("[0-9]+").matcher(password);
        Matcher specialCharacterMatcher = Pattern.compile("[!@#$%^&*()_+=]+").matcher(password);
        return upperCaseMatcher.find() && lowerCaseMatcher.find() && numberMatcher.find() && specialCharacterMatcher.find();
    }
}
