package vn.dk.BackendFoodApp.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailValidatorUtils {
    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
