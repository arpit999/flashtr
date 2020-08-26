package com.flashtr.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by hirenkanani on 29/12/15.
 */
public class SegoeuiTextViewBold extends TextView {

    public SegoeuiTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SegoeuiTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SegoeuiTextViewBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "SEGOEUIB.TTF");
        setTypeface(tf);
    }

}
