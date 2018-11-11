package com.example.kornel.alphaui.utils;

import java.util.regex.Pattern;

public class ProfileInfoValidator {
    private static final String NAME_REGEX = "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð,.'-]+$";
    private static final String SPECIAL_CHARS_REGEX = "[^a-z0-9 ]";
    private static final String UPPER_CASE_REGEX = "[A-Z ]";
    private static final String LOWER_CASE_REGEX = "[a-z ]";
    private static final String DIGITS_REGEX = "[0-9 ]";
    private static final int PASSWORD_MIN_LENGTH = 8;

    public static boolean isNameValid(String name) {
        Pattern namePattern = Pattern.compile(NAME_REGEX);
        return namePattern.matcher(name).find();
    }

    public static boolean isPasswordValid(String password) {
        Pattern specialCharsPattern = Pattern.compile(SPECIAL_CHARS_REGEX, Pattern.CASE_INSENSITIVE);
        Pattern upperCasePattern = Pattern.compile(UPPER_CASE_REGEX);
        Pattern lowerCasePattern = Pattern.compile(LOWER_CASE_REGEX);
        Pattern digitsPattern = Pattern.compile(DIGITS_REGEX);

        if (password.length() < PASSWORD_MIN_LENGTH) {
            return false;
        }
        if (!specialCharsPattern.matcher(password).find()) {
            return false;
        }
        if (!upperCasePattern.matcher(password).find()) {
            return false;
        }
        if (!lowerCasePattern.matcher(password).find()) {
            return false;
        }
        if (!digitsPattern.matcher(password).find()) {
            return false;
        }
        return true;
    }
}
