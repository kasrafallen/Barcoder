package ir.aravas.barcoder.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.google.gson.Gson;

import ir.aravas.barcoder.Rest;
import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.instance.DataInstance;
import ir.aravas.barcoder.view.LaunchView;
import ir.aravas.barcoder.Util;

public class LaunchActivity extends Activity {
    private static final int REQUEST_PERMISSION_CODE = 6969;

    private LaunchView launchView;
    private boolean animate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScanditLicense.setAppKey("iqtAfg3lU2oibJ6K8ZAAK1NuAMvL9W8jZOujX/vCJ1w");

        launchView = new LaunchView(this);
        setContentView(launchView.createBaseView());
        checkApp();
    }

    public void checkApp() {
        if (!Util.isDimen(this)) {
            setObserver();
            return;
        }
        if (!animate) {
            animate = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkApp();
                }
            }, 500);
            return;
        }
        if (!checkPermission()) {
            getPermission();
            return;
        }
        nextStep();
    }

    private void setObserver() {
        launchView.baseView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                launchView.baseView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Util.setDimen(LaunchActivity.this, new float[]{launchView.baseView.getWidth(), launchView.baseView.getHeight()});
                checkApp();
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED;
            if (!hasPermission) {
                return false;
            }
        }
        return true;
    }

    public void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.CAMERA}
                , REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (!checkDenied()) {
                checkApp();
            }
        }
    }

    private boolean checkDenied() {
        boolean hasDenied =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED;
        if (hasDenied) {
            launchView.showDeniedDialog();
        }
        return hasDenied;
    }

    private void nextStep() {
        printUser();
        if (!DataInstance.isSigned(this)) {
            BaseActivity.redirect(this, LoginActivity.class);
        } else {
            BaseActivity.redirect(this, ScanActivity.class);
        }
        finish();
    }

    private void printUser() {
        if (Application.IS_AZHAR) {
            Log.d(Rest.TAG, "printUser() returned: " + new Gson().toJson(DataInstance.getUserAzhar(this)));
        } else {
            Log.d(Rest.TAG, "printUser() returned: " + new Gson().toJson(DataInstance.getUser(this)));
        }
    }
}
