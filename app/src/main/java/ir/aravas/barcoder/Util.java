package ir.aravas.barcoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;

public class Util {

    private final static String WIDTH = "WIDTH";
    private final static String HEIGHT = "HEIGHT";
    private final static String SIGN = "SIGN";

    private static SharedPreferences getPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isSigned(Context context) {
        SharedPreferences preferences = getPreference(context);
        return preferences.getBoolean(SIGN, false);
    }

    public static void setSigned(Context context) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putBoolean(SIGN, true).apply();
    }

    public static boolean isDimen(Context context) {
        SharedPreferences preferences = getPreference(context);
        return preferences.contains(WIDTH) && preferences.contains(HEIGHT);
    }

    public static void setDimen(Context context, float[] floats) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putFloat(WIDTH, floats[0]);
        editor.putFloat(HEIGHT, floats[1]);
        editor.apply();
    }

    public static float[] getDimen(Context context) {
        SharedPreferences preferences = getPreference(context);
        return new float[]{preferences.getFloat(WIDTH, 0), preferences.getFloat(HEIGHT, 0)};
    }

    public static int getToolbarSize(Context context) {
        return toPx(56, context);
    }

    public static int toPx(int size, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

//    @SuppressLint("NewApi")
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public static void setRipple(RelativeLayout mid, Context context, boolean isDefault) {
//        int ripple_color;
//        if (isDefault) {
//            ripple_color = context.getResources().getColor(R.color.ripple_light);
//        } else {
//            ripple_color = context.getResources().getColor(R.color.ripple_dark);
//        }
//        ShapeDrawable shape = new ShapeDrawable(new OvalShape());
//        mid.setBackground(new RippleDrawable(new ColorStateList(
//                new int[][]
//                        {
//                                new int[]{android.R.attr.state_pressed},
//                                new int[]{}
//                        },
//                new int[]
//                        {
//                                ripple_color,
//                                ripple_color
//                        })
//                , null, shape));
//    }

    public static void setBackground(View view, Context context) {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        view.setBackgroundResource(backgroundResource);
        typedArray.recycle();
    }
}
