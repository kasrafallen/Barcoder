package ir.aravas.barcoder.util;

import android.app.Activity;
import android.app.Dialog;

public class DialogUtil {

    public static boolean show(Activity activity, Dialog dialog) {
        try {
            if (activity != null && dialog != null && !activity.isFinishing()) {
                dialog.show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
