package ir.aravas.barcoder.view;

import android.app.Activity;
import android.view.View;

import ir.aravas.barcoder.Util;

public abstract class BaseView {
    public float[] dimen;

    public BaseView(Activity activity){
        this.dimen = Util.getDimen(activity);
    }

    public abstract View createView();
}
