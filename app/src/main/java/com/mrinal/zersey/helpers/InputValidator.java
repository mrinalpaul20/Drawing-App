package com.mrinal.zersey.helpers;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

public class InputValidator {

    private static boolean inputError(EditText editText, String error) {
        editText.setError(error);
        editText.requestFocus();
        return false;
    }

    public static boolean validateEmail(EditText emailEt) {
        String email = emailEt.getText().toString().trim();
        if (TextUtils.isEmpty(email))
            return inputError(emailEt, "Email Required!");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return inputError(emailEt, "Invalid Email address!");
        return true;
    }

    public static boolean validatePassword(EditText passwordEt) {
        String password = passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(password))
            return inputError(passwordEt, "Password Required!");
        if (TextUtils.getTrimmedLength(password) < 6)
            return inputError(passwordEt, "Required min 6 characters");
        return true;
    }

    public static boolean validateName(EditText nameEt) {
        String name = nameEt.getText().toString().trim();
        if (TextUtils.isEmpty(name))
            return inputError(nameEt, "Name Required!");
        if (TextUtils.getTrimmedLength(name) < 3)
            return inputError(nameEt, "Required min 3 characters");
        return true;
    }
}