package ir.aravas.barcoder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;

import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.instance.DataInstance;
import ir.aravas.barcoder.modelazhar.LoginAzharModel;
import ir.aravas.barcoder.modelazhar.UserAzharModel;
import ir.aravas.barcoder.modelshahram.UserModel;
import ir.aravas.barcoder.util.FormUtil;
import ir.aravas.barcoder.util.PromptUtil;
import ir.aravas.barcoder.Rest;
import ir.aravas.barcoder.modelshahram.LoginModel;
import ir.aravas.barcoder.modelshahram.SignUpModel;
import ir.aravas.barcoder.modelazhar.SignUpAzharModel;
import ir.aravas.barcoder.view.LoginView;

public class LoginActivity extends BaseActivity implements View.OnClickListener, Rest.CallBack {

    private LoginView loginView;
    public ArrayList<TextInputLayout> list = new ArrayList<>();

    private final static String expectedDepSize = "<10";
    private String email;
    private String pass;
    private String name;
    private String company;
    private String phone;
    private String country;
    private String state;

    public PromptUtil util;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginView = new LoginView(this);
        setContentView(loginView.createView());
        loginView.start();

        util = new PromptUtil(this);

        if (Application.IS_AZHAR) {
            UserAzharModel userAzharModel = DataInstance.getUserAzhar(this);
            if (userAzharModel == null) {
                return;
            }
            if (userAzharModel.isVerified()) {
                loginView.showVerification(userAzharModel, LoginView.STEP_PASSWORD);
            } else {
                loginView.showVerification(userAzharModel, LoginView.STEP_VERIFICATION);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (checkFields()) {
            sendRequest();
        }
    }

    private boolean checkFields() {
        ArrayList<Boolean> validations = new ArrayList<>();
        if (loginView.current_position == LoginView.POSITION_SIGNUP) {
            validations.add(checkField(LoginView.NAME));
            validations.add(checkField(LoginView.EMAIL_SIGN));
            if (!Application.IS_AZHAR) {
                validations.add(checkField(LoginView.PASS_SIGN));
                validations.add(checkField(LoginView.RETRY));
            } else {
                validations.add(checkField(LoginView.COMPANY));
                validations.add(checkField(LoginView.COUNTRY));
                validations.add(checkField(LoginView.STATE));
                validations.add(checkField(LoginView.PHONE));
            }
        } else {
            validations.add(checkField(LoginView.EMAIL_LOG));
            validations.add(checkField(LoginView.PASS_LOG));
        }
        for (Boolean bool : validations) {
            if (!bool) {
                return false;
            }
        }
        return true;
    }

    private boolean checkField(String key) {
        for (TextInputLayout layout : list) {
            if (layout != null && layout.getTag() != null && key != null && layout.getTag().equals(key)) {
                boolean isValid = true;
                EditText editText = layout.getEditText();
                if (editText == null) {
                    isValid = false;
                } else {
                    String data = editText.getText().toString();
                    isValid = FormUtil.validate(data, key, layout);
                    if (isValid) {
                        fill(data, key);
                    }
                }
                return isValid;
            }
        }
        return false;
    }

    private void fill(String data, String key) {
        if (loginView.current_position == LoginView.POSITION_SIGNUP) {
            switch (key) {
                case LoginView.EMAIL_SIGN:
                    email = data;
                    break;
                case LoginView.PASS_SIGN:
                    pass = data;
                    break;
                case LoginView.NAME:
                    name = data;
                    break;
                case LoginView.COMPANY:
                    company = data;
                    break;
                case LoginView.STATE:
                    state = data;
                    break;
                case LoginView.COUNTRY:
                    country = data;
                    break;
                case LoginView.PHONE:
                    phone = data;
                    break;
            }
        } else {
            switch (key) {
                case LoginView.EMAIL_LOG:
                    email = data;
                    break;
                case LoginView.PASS_LOG:
                    pass = data;
                    break;
            }
        }
    }

    private void sendRequest() {
        if (loginView.current_position == LoginView.POSITION_SIGNUP) {
            String data;
            if (Application.IS_AZHAR) {
                data = new Gson().toJson(new SignUpAzharModel(email, name, ""
                        , company, phone, expectedDepSize, country, state));
            } else {
                data = new Gson().toJson(new SignUpModel(email, pass, name, "null"));
            }
            Rest.call(this, Rest.Method.SIGNUP, data, this);
        } else {
            String data;
            if (Application.IS_AZHAR) {
                data = new Gson().toJson(new LoginAzharModel(email, pass));
            } else {
                data = new Gson().toJson(new LoginModel(email, pass));
            }
            Rest.call(this, Rest.Method.LOGIN, data, new Rest.CallBack() {
                @Override
                public void onResponse(String data) {
                    util.hideDialog();
                    if (Application.IS_AZHAR) {
                        try {
                            UserAzharModel model = new Gson().fromJson(data, UserAzharModel.class);
                            model.setVerified(true);
                            model.setSigned(true);
                            model.setEmail(email);
                            model.setUserid(model.getUserId());
                            DataInstance.saveUserAzhar(LoginActivity.this, model);
                            BaseActivity.redirect(LoginActivity.this, ScanActivity.class);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            onError(null);
                        }
                    } else {
                        try {
                            UserModel model = new Gson().fromJson(data, UserModel.class);
                            DataInstance.saveUser(LoginActivity.this, model);
                            BaseActivity.redirect(LoginActivity.this, ScanActivity.class);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            onError(null);
                        }
                    }
                }

                @Override
                public void onInternet() {
                    LoginActivity.this.onInternet();
                }

                @Override
                public void onError(String data) {
                    LoginActivity.this.onError(data);

                }

                @Override
                public void onBefore() {
                    LoginActivity.this.onBefore();
                }
            });
        }
    }

    @Override
    public void onResponse(String data) {
        util.hideDialog();
        if (Application.IS_AZHAR) {
            UserAzharModel model = new Gson().fromJson(data, UserAzharModel.class);
            model.setEmail(email);
            DataInstance.saveUserAzhar(this, model);
            loginView.showVerification(model, LoginView.STEP_VERIFICATION);
        } else {
            UserModel model = new Gson().fromJson(data, UserModel.class);
            DataInstance.saveUser(this, model);
            BaseActivity.redirect(LoginActivity.this, ScanActivity.class);
            finish();
        }
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
