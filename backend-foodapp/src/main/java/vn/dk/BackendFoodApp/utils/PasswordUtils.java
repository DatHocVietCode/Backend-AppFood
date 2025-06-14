package vn.dk.BackendFoodApp.utils;

public class PasswordUtils {
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() <= 8) {
            return false;
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

}
