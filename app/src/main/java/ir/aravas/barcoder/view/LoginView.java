package ir.aravas.barcoder.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;

import ir.aravas.barcoder.Rest;
import ir.aravas.barcoder.activity.BaseActivity;
import ir.aravas.barcoder.activity.ScanActivity;
import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.R;
import ir.aravas.barcoder.Util;
import ir.aravas.barcoder.activity.LoginActivity;
import ir.aravas.barcoder.instance.DataInstance;
import ir.aravas.barcoder.modelazhar.ActivateAzharModel;
import ir.aravas.barcoder.modelazhar.UserAzharModel;
import ir.aravas.barcoder.util.DialogUtil;

public class LoginView extends BaseView {

    private static final String[] TITLE = new String[]{"Login", "Signup"};
    private static final int FUNCTION_ID = +12456999;

    public static final String EMAIL_LOG = "Enter a valid email";
    public static final String EMAIL_SIGN = "Enter a valid email ";

    public static final String PASS_LOG = "Enter your password ";
    public static final String PASS_SIGN = "Enter your password";

    public static final String RETRY = "Confirm your password";
    public static final String NAME = "Enter your full name";
    public static final String PHONE = "Enter your phone number";
    public static final String COMPANY = "Enter your company name (optional)";
    public static final String STATE = "Enter your state (optional)";
    public static final String COUNTRY = "Enter your country (optional)";

    public static final int POSITION_LOGIN = 0;
    public static final int POSITION_SIGNUP = 1;

    public static final int STEP_PASSWORD = 4;
    public static final int STEP_VERIFICATION = 3;

    private LoginActivity context;
    private int function;
    private ViewPager pager;
    private int indicator;
    private PagerSlidingTabStrip tabStrip;
    private int margin;

    public int current_position;
    public LinearLayout layout;

    public LoginView(Activity loginActivity) {
        super(loginActivity);
        this.context = (LoginActivity) loginActivity;
        this.function = Util.getToolbarSize(context);
        this.indicator = Util.toPx(2, context);
        this.margin = Util.toPx(15, context);
    }

    @Override
    public View createView() {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        layout.setBackgroundResource(R.color.base);

        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        params.addRule(RelativeLayout.ABOVE, FUNCTION_ID);
        box.setLayoutParams(params);
        box.addView(toolbar());
        box.addView(pager());

        layout.addView(function());
        layout.addView(box);
        return layout;
    }

    private View toolbar() {
        tabStrip = new PagerSlidingTabStrip(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabStrip.setElevation(12);
        }
        tabStrip.setBackgroundResource(R.color.toolbar);
        tabStrip.setLayoutParams(new LinearLayout.LayoutParams(-1, Util.getToolbarSize(context)));
        tabStrip.setUnderlineColor(Color.TRANSPARENT);
        tabStrip.setIndicatorHeight(indicator);
        tabStrip.setShouldExpand(true);
        tabStrip.setAllCaps(false);
        tabStrip.setIndicatorColor(Color.WHITE);
        tabStrip.setTextSize(Util.toPx(12, context));
        tabStrip.setTextColor(Color.WHITE);
        return tabStrip;
    }

    private View function() {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setId(FUNCTION_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.setElevation(12);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, function);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.setLayoutParams(params);
        layout.setBackgroundResource(R.color.toolbar);

        TextView text = new TextView(context);
        text.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        text.setGravity(Gravity.CENTER);
        Util.setBackground(text, context);
        text.setTextColor(Color.WHITE);
        text.setTextSize(1, 15);
        text.setText("Continue");
        text.setOnClickListener(context);
        text.setTypeface(text.getTypeface(), Typeface.BOLD);

        layout.addView(text);
        return layout;
    }

    private View pager() {
        pager = new ViewPager(context);
        pager.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1f));
        return pager;
    }

    public void start() {
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return TITLE.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }


            @Override
            public CharSequence getPageTitle(int position) {
                return TITLE[position];
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = createView(position);
                container.addView(view, 0);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        tabStrip.setViewPager(pager);
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_position = position;
                selectForm(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        selectForm(POSITION_LOGIN, true);
    }

    private View createView(int position) {
        ScrollView scrollView = new ScrollView(context);

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        if (position == POSITION_LOGIN) {
            layout.addView(form(EMAIL_LOG));
            layout.addView(form(PASS_LOG));
        } else {
            layout.addView(form(NAME));
            layout.addView(form(EMAIL_SIGN));
            if (Application.IS_AZHAR) {
                layout.addView(form(PHONE));
                layout.addView(form(COMPANY));
                layout.addView(form(COUNTRY));
                layout.addView(form(STATE));
            } else {
                layout.addView(form(PASS_SIGN));
                layout.addView(form(RETRY));
            }
        }

        scrollView.addView(layout);
        return scrollView;
    }

    private View form(final String hint) {
        TextInputLayout layout = new TextInputLayout(context);
        layout.setTag(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(margin, margin, margin, margin);
        layout.setLayoutParams(params);

        final AppCompatEditText text = new AppCompatEditText(context);
        TextInputLayout.LayoutParams params1 = new AppBarLayout.LayoutParams(-1, -2);
        params1.gravity = Gravity.CENTER_VERTICAL;
        text.setLayoutParams(params1);
        text.setTextSize(1, 12f);
        text.setHint(hint);
        text.setHintTextColor(Color.GRAY);
        text.setTextColor(Color.DKGRAY);
        text.setSingleLine();
        text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (hint.equals(PHONE)) {
                        text.setTextSize(1, 17f);
                    } else {
                        text.setTextSize(1, 15f);
                    }
                } else {
                    text.setTextSize(1, 12f);
                }
            }
        });
        switch (hint) {
            case EMAIL_LOG:
            case EMAIL_SIGN:
                text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case PHONE:
                text.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case PASS_LOG:
            case PASS_SIGN:
            case RETRY:
                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
        layout.addView(text);

        context.list.add(layout);
        return layout;
    }

    private void selectForm(int position, boolean changeBackground) {
        LinearLayout layout = (LinearLayout) tabStrip.getChildAt(0);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view != null && view instanceof TextView) {
                if (i == position) {
                    ((TextView) view).setTextColor(Color.WHITE);
                } else {
                    ((TextView) view).setTextColor(Color.LTGRAY);
                }
                if (changeBackground) {
                    Util.setBackground(view, context);
                }
            }
        }
    }

    public void showVerification(final UserAzharModel model, final int step) {
        final LinearLayout view = createView(model, step);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (step == STEP_VERIFICATION) {
            builder.setPositiveButton("VERIFY YOUR ACCOUNT", null);
            builder.setMessage("We sent a verification pin code to your email, enter the pin and verify your account");
        } else {
            builder.setPositiveButton("Update your profile", null);
            builder.setMessage("Now you can Enter your chosen password, enter and confirm the password and update your profile");
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (step == STEP_VERIFICATION) {
                            check(view.getChildAt(0), model, dialog);
                        } else {
                            check(view.getChildAt(0), view.getChildAt(1), model, dialog);
                        }
                    }
                });
            }
        });
        DialogUtil.show(context, dialog);
    }

    private void check(View childAt, View childAt1, UserAzharModel model, DialogInterface dialog) {
        AppCompatEditText text = (AppCompatEditText) childAt;
        AppCompatEditText text1 = (AppCompatEditText) childAt1;
        if (text.getText().toString().equalsIgnoreCase(text1.getText().toString())) {
            if (text.getText().length() >= 6) {
                sendRequest(model, text.getText().toString(), dialog);
            } else {
                text.setError("Minimum number of characters should be 6");
                text1.setError("Minimum number of characters should be 6");
            }
        } else {
            text.setError("Passwords do no match");
            text1.setError("Passwords do no match");
        }
    }

    private void check(View view, UserAzharModel model, DialogInterface dialog) {
        AppCompatEditText text = (AppCompatEditText) view;
        if (model != null && text.getText().toString().equalsIgnoreCase(String.valueOf(model.getPincode()))) {
            dialog.dismiss();
            model.setVerified(true);
            DataInstance.saveUserAzhar(context, model);
            showVerification(model, STEP_PASSWORD);
        } else {
            text.setError("Pin code is not correct");
        }
    }

    private LinearLayout createView(UserAzharModel model, int step) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        if (step == STEP_VERIFICATION) {
            layout.addView(editText(0));
        } else {
            layout.addView(editText(1));
            layout.addView(editText(2));
        }
        return layout;
    }

    private View editText(final int mode) {
        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setTextColor(Color.DKGRAY);
        editText.setHintTextColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(margin, margin * 2, margin, margin * 2);
        editText.setLayoutParams(params);
        switch (mode) {
            case 0:
                editText.setHint("ENTER PIN CODE");
                break;
            case 1:
                editText.setHint("Enter your password");
                break;
            case 2:
                editText.setHint("Re-enter your password");
                break;
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editText.setSingleLine();
        editText.setTextSize(1, 12f);
        if (mode == 0) {
            editText.setGravity(Gravity.CENTER);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (mode == 0) {
                        editText.setTextSize(1, 19f);
                    } else {
                        editText.setTextSize(1, 15f);
                    }
                } else {
                    editText.setTextSize(1, 12f);
                }
            }
        });
        return editText;
    }

    private void sendRequest(final UserAzharModel model, String password, final DialogInterface dialog) {
        Rest.call(context, Rest.Method.ACTIVATE, new Gson().toJson(new ActivateAzharModel(model.getUserid(), password)), new Rest.CallBack() {
            @Override
            public void onResponse(String data) {
                context.util.hideDialog();
                dialog.dismiss();
                model.setSigned(true);
                DataInstance.saveUserAzhar(context, model);
                BaseActivity.redirect(context, ScanActivity.class);
                context.finish();
            }

            @Override
            public void onInternet() {
                context.onInternet();
            }

            @Override
            public void onError(String data) {
                context.onError(data);
            }

            @Override
            public void onBefore() {
                context.onBefore();
            }
        });
    }
}
