package ir.aravas.barcoder.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import ir.aravas.barcoder.util.DialogUtil;
import ir.aravas.barcoder.R;
import ir.aravas.barcoder.activity.LaunchActivity;

public class LaunchView {
    private LaunchActivity context;
    public RelativeLayout baseView;

    public LaunchView(LaunchActivity context) {
        this.context = context;
    }

    public View createBaseView() {
        baseView = new RelativeLayout(context);
        baseView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        baseView.setBackgroundResource(R.color.toolbar);

        ProgressBar bar = new ProgressBar(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2,-2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        bar.setLayoutParams(params);

        baseView.addView(bar);
        return baseView;
    }

    public void showDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher).setMessage("Application requires allowed accesses to work, allow permissions to continue.")
                .setTitle(context.getResources().getString(R.string.app_name)).setPositiveButton("Settings", null);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.checkApp();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.onBackPressed();
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            final Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + context.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        DialogUtil.show(context, dialog);
    }
}
