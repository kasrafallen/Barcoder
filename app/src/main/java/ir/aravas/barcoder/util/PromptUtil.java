package ir.aravas.barcoder.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class PromptUtil {
    public static final String WAITING = "Please wait ...";
    public static final String INTERNET = "Internet connection not found";
    public static final String ERROR = "Something went wrong, Please try again";

    private AlertDialog dialog;
    private Activity context;

    public interface CallBack {
        void onClick();
    }

    public PromptUtil(Activity context) {
        this.context = context;
    }

    public void createDialog(String mode, final CallBack callBack, String input) {
        hideDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (input != null) {
            builder.setMessage(input);
        } else {
            builder.setMessage(mode);
        }
        if (!mode.equals(WAITING)) {
            if(input == null) {
                builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callBack.onClick();
                    }
                });
            }
            builder.setNegativeButton("Return", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog = builder.create();
        if (mode.equals(WAITING)) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        DialogUtil.show(context, dialog);
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
