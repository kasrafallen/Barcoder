package ir.aravas.barcoder.util;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.apache.commons.validator.routines.EmailValidator;

import ir.aravas.barcoder.view.LoginView;

public class FormUtil {

    public static boolean validate(String data, String key, TextInputLayout layout) {
        boolean isValid = true;
        switch (key) {
            case LoginView.PASS_LOG:
            case LoginView.PASS_SIGN:
                if (data.length() < 6) {
                    isValid = false;
                    layout.setErrorEnabled(true);
                    layout.setError("Minimum number of characters should be 6");
                } else {
                    layout.setErrorEnabled(false);
                }
                break;
            case LoginView.EMAIL_LOG:
            case LoginView.EMAIL_SIGN:
                if (!isEmailValid(data)) {
                    isValid = false;
                    layout.setErrorEnabled(true);
                    layout.setError("Email is not valid");
                } else {
                    layout.setErrorEnabled(false);
                }
                break;
            case LoginView.RETRY:
                if (data.length() < 6 || !data.equalsIgnoreCase(getPass(layout))) {
                    isValid = false;
                    layout.setErrorEnabled(true);
                    layout.setError("Passwords do no match");
                } else {
                    layout.setErrorEnabled(false);
                }
                break;
            case LoginView.NAME:
                if (data.length() < 3) {
                    isValid = false;
                    layout.setErrorEnabled(true);
                    layout.setError("Minimum number of characters should be 3");
                } else {
                    layout.setErrorEnabled(false);
                }
                break;
            case LoginView.PHONE:
                if (data.length() < 5) {
                    isValid = false;
                    layout.setErrorEnabled(true);
                    layout.setError("Phone number is not valid");
                } else {
                    layout.setErrorEnabled(false);
                }
                break;
        }
        return isValid;
    }

    private static String getPass(TextInputLayout layout) {
        try {
            LinearLayout box = (LinearLayout) layout.getParent();
            TextInputLayout layout1 = (TextInputLayout) box.findViewWithTag(LoginView.PASS_SIGN);
            EditText editText = layout1.getEditText();
            return editText.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isEmailValid(String data) {
        return EmailValidator.getInstance().isValid(data);
    }
}
