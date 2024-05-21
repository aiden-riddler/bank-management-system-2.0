package com.example.bms;

public class InputValidation {
    public static boolean emailValidate(String email) {
        if (!email.trim().contains("@") || !email.trim().endsWith(".com"))
            return false;
        return true;
    }

    public static boolean passwordValidate(String password){
        return password.trim().length() > 4;
    }

    public static boolean confirmPassword(String password, String cPassword){ return password.trim().equals(cPassword.trim()); }

    public static boolean phoneValidate(String phone) { return phone.trim().length() == 9; }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

}
