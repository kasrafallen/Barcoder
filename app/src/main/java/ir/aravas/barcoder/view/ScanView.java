package ir.aravas.barcoder.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import ir.aravas.barcoder.R;
import ir.aravas.barcoder.Rest;
import ir.aravas.barcoder.Util;
import ir.aravas.barcoder.activity.ScanActivity;
import ir.aravas.barcoder.application.Application;
import ir.aravas.barcoder.modelazhar.ProductAzharModel;
import ir.aravas.barcoder.modelshahram.InviteModel;
import ir.aravas.barcoder.modelshahram.ProductModel;
import ir.aravas.barcoder.util.DialogUtil;
import ir.aravas.barcoder.util.FormUtil;

public class ScanView extends BaseView {
    private static final String[] TITLE = new String[]{"Products", "Invitations"};
    private int current_position;

    private static final int FUNCTION_ID = +12458888;
    private ScanActivity context;
    private int margin;
    private int function;
    private ViewPager pager;
    private PagerSlidingTabStrip tabStrip;
    private int indicator;
    private TextView text;

    public ScanView(Activity activity) {
        super(activity);
        this.context = (ScanActivity) activity;
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

    private View pager() {
        pager = new ViewPager(context);
        pager.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1f));
        return pager;
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

        text = new TextView(context);
        text.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        text.setGravity(Gravity.CENTER);
        Util.setBackground(text, context);
        text.setTextColor(Color.WHITE);
        text.setTextSize(1, 15);
        text.setText("Open Scanner");
        text.setTypeface(text.getTypeface(), Typeface.BOLD);
        text.setOnClickListener(context);

        layout.addView(text);
        return layout;
    }

    public void start() {
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                if (Application.IS_AZHAR) {
                    return TITLE.length - 1;
                } else {
                    return TITLE.length;
                }
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
                if (position == 0) {
                    text.setText("Open Scanner");
                } else {
                    text.setText("Send Invitation");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setCurrentItem(current_position);
        selectForm(current_position, true);
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

    private View createView(int position) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = new LinearLayout(context);
        if (position == 0) {
            if (Application.IS_AZHAR) {
                if (context.userModelAzhar.getDevice() != null && context.userModelAzhar.getDevice().length > 0) {
                    int counter = 0;
                    for (int i = context.userModelAzhar.getDevice().length - 1; i > -1; i--) {
                        layout.addView(product(context.userModelAzhar.getDevice()[i], counter));
                        counter++;
                    }
                }
            } else {
                if (context.userModelShahram.getCheckedProductsByBarcode() != null && context.userModelShahram.getCheckedProductsByBarcode().length > 0) {
                    int counter = 0;
                    for (int i = context.userModelShahram.getCheckedProductsByBarcode().length - 1; i > -1; i--) {
                        layout.addView(product(context.userModelShahram.getCheckedProductsByBarcode()[i], counter));
                        counter++;
                    }
                }
            }
        } else {
            if (context.userModelShahram.getInviteList() != null && context.userModelShahram.getInviteList().length > 0) {
                int counter = 0;
                for (String email : context.userModelShahram.getInviteList()) {
                    layout.addView(invitation(email, counter));
                    counter++;
                }
            }
        }
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);
        return scrollView;
    }

    private View product(ProductAzharModel product, int position) {
        CardView cardView = new CardView(context);
        cardView.setCardElevation(3);
        cardView.setRadius(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        if (position == 0) {
            params.setMargins(margin, margin, margin, margin);
        } else {
            params.setMargins(margin, 0, margin, margin);
        }
        cardView.setLayoutParams(params);

        TextView textView = new TextView(context);
        textView.setTextColor(Color.DKGRAY);
        textView.setTextSize(1, 12);
        CardView.LayoutParams param = new FrameLayout.LayoutParams(-1, -2);
        param.gravity = Gravity.CENTER_VERTICAL;
        param.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(param);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        SpannableString span = new SpannableString(product.getLicenseKey()
                + "\n\n" + product.getEmail()
                + "\n" + "Os: " + product.getOs() + "   " + "Os version: " + product.getVersion()
        );
        span.setSpan(new AbsoluteSizeSpan(15, true)
                , 0, span.toString().indexOf("\n\n"), 0);
        span.setSpan(new AbsoluteSizeSpan(18, true)
                , span.toString().indexOf("Os: ") + 4, span.toString().indexOf("Os version: "), 0);
        span.setSpan(new AbsoluteSizeSpan(18, true)
                , span.toString().indexOf("Os version: ") + 12, span.length(), 0);
        textView.setText(span);

        cardView.addView(textView);
        return cardView;
    }

    private View product(ProductModel product, int position) {
        CardView cardView = new CardView(context);
        cardView.setCardElevation(3);
        cardView.setRadius(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        if (position == 0) {
            params.setMargins(margin, margin, margin, margin);
        } else {
            params.setMargins(margin, 0, margin, margin);
        }
        cardView.setLayoutParams(params);

        TextView textView = new TextView(context);
        textView.setTextColor(Color.DKGRAY);
        textView.setTextSize(1, 12);
        CardView.LayoutParams param = new FrameLayout.LayoutParams(-1, -2);
        param.gravity = Gravity.CENTER_VERTICAL;
        param.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(param);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        SpannableString span = new SpannableString(product.getTitle()
                + "\n\n" + product.getDescription()
                + "\n" + "Price: " + product.getPrice() + "   " + "Discount: " + product.getDiscount()
        );
        span.setSpan(new AbsoluteSizeSpan(15, true)
                , 0, span.toString().indexOf("\n\n"), 0);
        span.setSpan(new AbsoluteSizeSpan(18, true)
                , span.toString().indexOf("Price: ") + 7, span.toString().indexOf("Discount: "), 0);
        span.setSpan(new AbsoluteSizeSpan(18, true)
                , span.toString().indexOf("Discount: ") + 10, span.length(), 0);
        textView.setText(span);

        cardView.addView(textView);
        return cardView;
    }

    private View invitation(String email, int position) {
        CardView cardView = new CardView(context);
        cardView.setCardElevation(3);
        cardView.setRadius(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        if (position == 0) {
            params.setMargins(margin, margin, margin, margin);
        } else {
            params.setMargins(margin, 0, margin, margin);
        }
        cardView.setLayoutParams(params);

        TextView textView = new TextView(context);
        textView.setTextColor(Color.DKGRAY);
        textView.setTextSize(1, 13);
        textView.setSingleLine();
        textView.setText(email);
        CardView.LayoutParams param = new FrameLayout.LayoutParams(-1, -2);
        param.gravity = Gravity.CENTER_VERTICAL;
        param.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(param);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        cardView.addView(textView);
        return cardView;
    }

    public void showInvite() {
        final LinearLayout view = createDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Send Invitation", null);
        builder.setMessage("You can invite others by entering their email address and send invitation");
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
                        check(view.getChildAt(0), dialog);
                    }
                });
            }
        });
        DialogUtil.show(context, dialog);
    }

    private LinearLayout createDialog() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setTextColor(Color.DKGRAY);
        editText.setHintTextColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(margin, margin * 2, margin, margin * 2);
        editText.setLayoutParams(params);
        editText.setHint("Enter a valid email address");
        editText.setSingleLine();
        editText.setTextSize(1, 12f);
        editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
                    editText.setTextSize(1, 15f);
                } else {
                    editText.setTextSize(1, 12f);
                }
            }
        });
        layout.addView(editText);
        return layout;
    }

    private void check(View view, DialogInterface dialog) {
        AppCompatEditText text = (AppCompatEditText) view;
        if (FormUtil.isEmailValid(text.getText().toString())) {
            sendRequest(text.getText().toString(), dialog);
        } else {
            text.setError("Email address is not correct");
        }
    }

    private void sendRequest(String email, final DialogInterface dialog) {
        Rest.call(context, Rest.Method.INVITE, new Gson().toJson(new InviteModel(email)), new Rest.CallBack() {
            @Override
            public void onResponse(String data) {
                context.util.hideDialog();
                dialog.dismiss();
                Toast.makeText(context, "Invitation has been send successfully", Toast.LENGTH_SHORT).show();
                context.sendRequest();
            }

            @Override
            public void onInternet() {
                context.onInternet();
            }

            @Override
            public void onError(String data) {
//                context.onError(data);
                context.util.hideDialog();
                dialog.dismiss();
                Toast.makeText(context, "Invitation has been send successfully", Toast.LENGTH_SHORT).show();
                context.sendRequest();
            }

            @Override
            public void onBefore() {
                context.onBefore();
            }
        });
    }

    public void showConfirm(Barcode barcode) {
        final LinearLayout view = createWebView(barcode);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Refresh", null);
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
                        if (view != null && view.getChildCount() > 1 && view.getChildAt(1) != null
                                && view.getChildAt(1) instanceof WebView) {
                            ((WebView) view.getChildAt(1)).reload();
                        }
                    }
                });
            }
        });
        DialogUtil.show(context, dialog);
    }

    private LinearLayout createWebView(Barcode barcode) {
        String url;
        if (barcode.displayValue.startsWith("http") || barcode.displayValue.startsWith("www")) {
            url = barcode.displayValue;
        } else {
            url = "http://jstarpass.com:8080/scan/barcode/" + barcode.displayValue + "?confirm=true&qty=1";
        }

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        textView.setTextColor(Color.DKGRAY);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setText(url);

        WebView webView = new WebView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, (int) (4f * dimen[1] / 6f));
        webView.setNetworkAvailable(true);
        webView.setBackgroundColor(Color.WHITE);
        webView.setLayoutParams(params);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        layout.addView(textView);
        layout.addView(webView);
        int padding = (int) (dimen[1] / 50f);
        textView.setPadding(padding, padding, padding, padding);
        return layout;
    }
}
