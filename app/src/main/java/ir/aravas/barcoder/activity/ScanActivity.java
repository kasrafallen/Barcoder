package ir.aravas.barcoder.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import ir.aravas.barcoder.R;
import ir.aravas.barcoder.Rest;
import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.instance.DataInstance;
import ir.aravas.barcoder.modelazhar.ProductAzharModel;
import ir.aravas.barcoder.modelazhar.UserAzharModel;
import ir.aravas.barcoder.modelshahram.UserModel;
import ir.aravas.barcoder.util.PromptUtil;
import ir.aravas.barcoder.view.ScanView;

public class ScanActivity extends BaseActivity implements View.OnClickListener, Rest.CallBack {

    private final static String TAG = "ScanActivity";
    private ScanView scanView;
    public PromptUtil util;
    public UserModel userModelShahram;
    public UserAzharModel userModelAzhar;

    private boolean isSending;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanView = new ScanView(this);
        setContentView(scanView.createView());
        util = new PromptUtil(this);
        sendRequest();
    }

    public void sendRequest() {
        if (Application.IS_AZHAR) {
            UserAzharModel userAzharModel = DataInstance.getUserAzhar(this);
            if (userAzharModel.getUserid() > userAzharModel.getUserId()) {
                userAzharModel.setUserId(userAzharModel.getUserid());
            } else {
                userAzharModel.setUserid(userAzharModel.getUserId());
            }
            Rest.call(this, Rest.Method.PROFILE, new Gson().toJson(userAzharModel), this);
        } else {
            Rest.call(this, Rest.Method.PROFILE, new Gson().toJson(DataInstance.getUser(this)), this);
        }
    }

    private void scanner() {
        MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withCenterTracker(R.mipmap.frame_unselected, R.mipmap.frame_selected)
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        setResult(barcode);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    private void setResult(final Barcode barcode) {
        if (barcode == null || barcode.displayValue == null) {
            Toast.makeText(this, "Did not scanned successfully", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSending) {
            return;
        }
        Log.e(TAG, "onResult() returned: " + new Gson().toJson(barcode));
        String data;
        if (Application.IS_AZHAR) {
            data = new Gson().toJson(new ProductAzharModel(barcode.displayValue
                    , DataInstance.getUserAzhar(this).getEmail(),
                    String.valueOf(Build.VERSION.SDK_INT)));
        } else {
            data = barcode.displayValue;
        }

        boolean isValid = true;
        for (char character : data.toCharArray()) {
            if (!Character.isDigit(character)) {
                isValid = false;
            }
        }
        if (!isValid) {
            onError("Barcode type is not registered");
            isSending = false;
        }
        openWebConfirmation(barcode);
        if (!isValid) {
            return;
        }
        Rest.call(this, Rest.Method.SCAN, data, new Rest.CallBack() {
            @Override
            public void onResponse(String data) {
                isSending = false;
                util.hideDialog();
                sendRequest();
            }

            @Override
            public void onInternet() {
                ScanActivity.this.onInternet();
                isSending = false;
            }

            @Override
            public void onError(String data) {
                ScanActivity.this.onError(data);
                isSending = false;
            }

            @Override
            public void onBefore() {
                isSending = true;
                ScanActivity.this.onBefore();
            }
        });
    }

    private void openWebConfirmation(Barcode barcode) {
        scanView.showConfirm(barcode);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (((TextView) view).getText().equals("Open Scanner")) {
            scanner();
        } else {
            scanView.showInvite();
        }
    }

    @Override
    public void onResponse(String data) {
        util.hideDialog();
        if (Application.IS_AZHAR) {
            userModelAzhar = new Gson().fromJson(data, UserAzharModel.class);
        } else {
            userModelShahram = new Gson().fromJson(data, UserModel.class);
        }
        if (userModelAzhar == null && userModelShahram == null) {
            onError(null);
            return;
        }
        scanView.start();
    }

    @Override
    public void onInternet() {
        util.createDialog(PromptUtil.INTERNET, new PromptUtil.CallBack() {
            @Override
            public void onClick() {
                sendRequest();
            }
        }, null);
    }

    @Override
    public void onError(String data) {
        util.createDialog(PromptUtil.ERROR, new PromptUtil.CallBack() {
            @Override
            public void onClick() {
                sendRequest();
            }
        }, data);
    }

    @Override
    public void onBefore() {
        util.createDialog(PromptUtil.WAITING, null, null);
    }
}
