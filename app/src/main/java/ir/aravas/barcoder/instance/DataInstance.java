package ir.aravas.barcoder.instance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import ir.aravas.barcoder.activity.LaunchActivity;
import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.modelazhar.UserAzharModel;
import ir.aravas.barcoder.modelshahram.UserModel;

public class DataInstance {

    private static final String AZHAR_USER = "AZHAR_USER";
    private static final String SHAHRAM_USER = "SHAHRAM_USER";

    private static SharedPreferences getPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveUserAzhar(Context context, UserAzharModel model) {
        getPreference(context).edit().putString(AZHAR_USER, new Gson().toJson(model)).apply();
    }

    public static UserAzharModel getUserAzhar(Context context) {
        return new Gson().fromJson(getPreference(context).getString(AZHAR_USER, null), UserAzharModel.class);
    }

    public static boolean isSigned(LaunchActivity context) {
        if (Application.IS_AZHAR) {
            UserAzharModel model = getUserAzhar(context);
            if (model != null && model.isSigned()) {
                return true;
            }
        } else {
            if (getUser(context) != null) {
                return true;
            }
        }
        return false;
    }

    public static UserModel getUser(Context context) {
        return new Gson().fromJson(getPreference(context).getString(SHAHRAM_USER, null), UserModel.class);
    }

    public static void saveUser(Context context, UserModel model) {
        getPreference(context).edit().putString(SHAHRAM_USER, new Gson().toJson(model)).apply();
    }
}
